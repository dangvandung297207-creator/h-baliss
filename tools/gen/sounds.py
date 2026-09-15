"""Procedural sound synthesis for Herbalist's Craft.

Minecraft needs Ogg Vorbis, and no encoder is guaranteed to exist on a build machine, so
this module is optional: if ``numpy`` and ``soundfile`` are available the sounds are
synthesised exactly like the textures are drawn (deterministic, generated, committed), and
otherwise the generator keeps whatever files already exist and says so.

Run it with a virtualenv that has the two packages:

    python3 -m venv .venv && .venv/bin/pip install numpy soundfile
    .venv/bin/python tools/generate.py
"""
from __future__ import annotations

import pathlib

SAMPLE_RATE = 44100


def _require():
    try:
        import numpy as np  # noqa: F401
        import soundfile as sf  # noqa: F401
        return np, sf
    except Exception:
        return None, None


def _noise(np, samples: int, rng):
    return rng.standard_normal(samples)


def _lowpass(signal, alpha: float):
    out = signal.copy()
    acc = 0.0
    for i in range(len(signal)):
        acc += alpha * (signal[i] - acc)
        out[i] = acc
    return out


def _highpass(signal, alpha: float):
    return signal - _lowpass(signal, alpha)


def _envelope(np, samples: int, attack: float, release: float):
    attack_n = max(1, int(samples * attack))
    release_n = max(1, int(samples * release))
    env = np.ones(samples)
    env[:attack_n] = np.linspace(0.0, 1.0, attack_n)
    env[-release_n:] = np.linspace(1.0, 0.0, release_n)
    return env


def _sounds(np):
    """Returns a dict of sound name -> float numpy array (mono, SAMPLE_RATE)."""
    rng = np.random.default_rng(20240915)
    out = {}

    # --- mortar grinding: crushed mineral noise with a slow grind rhythm -----------
    n = int(SAMPLE_RATE * 1.4)
    t = np.arange(n) / SAMPLE_RATE
    base = _lowpass(_highpass(_noise(np, n, rng), 0.25), 0.35)
    grind_mod = 0.55 + 0.45 * np.sin(2 * np.pi * 2.6 * t)
    out["grind"] = base * grind_mod * 0.5

    # --- milling stones: looping scrape with two swells ---------------------------
    n = int(SAMPLE_RATE * 1.6)
    t = np.arange(n) / SAMPLE_RATE
    scrape = _lowpass(_noise(np, n, rng), 0.5)
    swell = 0.4 + 0.6 * np.abs(np.sin(2 * np.pi * 0.8 * t))
    wobble = np.sin(2 * np.pi * 42 * t) * 0.25 + 0.75
    out["mill"] = scrape * swell * wobble * 0.45

    # --- pouring water: bright noise with bubbling blips -------------------------
    n = int(SAMPLE_RATE * 0.9)
    pour = _highpass(_noise(np, n, rng), 0.35) * _envelope(np, n, 0.1, 0.45) * 0.35
    for start in (0.12, 0.3, 0.52, 0.71):
        i = int(start * SAMPLE_RATE)
        length = int(SAMPLE_RATE * 0.05)
        blip = np.sin(2 * np.pi * 900 * (np.arange(length) / SAMPLE_RATE)) * np.linspace(0.25, 0.0, length)
        pour[i:i + length] += blip
    out["pour"] = pour

    # --- drying herbs: dry leaf rustle -------------------------------------------
    n = int(SAMPLE_RATE * 1.0)
    rustle = _highpass(_noise(np, n, rng), 0.55).copy()
    for k in range(6):
        i = int(SAMPLE_RATE * (0.05 + k * 0.14))
        length = int(SAMPLE_RATE * 0.09)
        rustle[i:i + length] *= np.linspace(0.1, 1.0, length)
    out["dry"] = rustle * 0.22

    # --- applying salve: soft low smear ------------------------------------------
    n = int(SAMPLE_RATE * 0.6)
    salve = _lowpass(_noise(np, n, rng), 0.12) * _envelope(np, n, 0.25, 0.5) * 0.6
    out["salve"] = salve

    # --- knowledge recorded: gentle two-note chime -------------------------------
    n = int(SAMPLE_RATE * 1.2)
    t = np.arange(n) / SAMPLE_RATE
    chime = np.zeros(n)
    for freq, amp, decay in ((659.25, 0.5, 3.2), (987.77, 0.32, 3.6), (1318.5, 0.16, 4.2)):
        chime += amp * np.sin(2 * np.pi * freq * t) * np.exp(-decay * t)
    chime += 0.05 * _highpass(_noise(np, n, rng), 0.75) * np.exp(-14 * t)
    out["discovery"] = chime * 0.5

    # --- sipping tea: short muffled sip ------------------------------------------
    n = int(SAMPLE_RATE * 0.5)
    t = np.arange(n) / SAMPLE_RATE
    sip = _lowpass(_noise(np, n, rng), 0.3) * _envelope(np, n, 0.12, 0.6)
    sip *= 0.6 + 0.4 * np.sin(2 * np.pi * 9 * t)
    out["sip"] = sip * 0.35

    return out


def generate_sounds(root: pathlib.Path, log=print) -> bool:
    """Writes ``assets/herbalistscraft/sounds/<name>.ogg``. Returns True on success."""
    np, sf = _require()
    target = root / "src/main/resources/assets/herbalistscraft/sounds"
    if np is None or sf is None:
        log("  ! numpy/soundfile unavailable - keeping existing sound files (see tools/gen/sounds.py)")
        return target.exists()
    target.mkdir(parents=True, exist_ok=True)
    for name, data in _sounds(np).items():
        peak = float(np.max(np.abs(data))) or 1.0
        data = (data / peak) * 0.55
        sf.write(target / f"{name}.ogg", data.astype("float32"), SAMPLE_RATE,
                 format="OGG", subtype="VORBIS")
    log(f"  sounds: {len(_sounds(np))} ogg files written to {target.relative_to(root)}")
    return True
