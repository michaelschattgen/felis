# Felis

Felis is a free, ad-free Android link cleaner focused on privacy and fast sharing.
It removes tracking parameters from links before you copy, share, or paste them.

## Why Felis

- Clean noisy URLs in one tap
- Share privacy-friendlier links from anywhere
- Keep useful link analytics locally on-device
- Stay lightweight: no account, no cloud sync, no ads

## Features

- **Link cleaning engine**
  - Strips common tracking params (`utm_*`, `fbclid`, `gclid`, `si` and more)
  - Special handling for selected domains (for example Spotify, Instagram, Amazon)
  - Cleans URLs inside full text blocks, not just single links
- **Multiple entry points**
  - Android Share Sheet (`ACTION_SEND`)
  - Text Selection context action (`ACTION_PROCESS_TEXT`)
  - In-app clean flow from clipboard/manual input
- **History and stats**
  - Lifetime totals for cleaned URLs and removed parameters
  - Top cleaned domains with last-cleaned timestamps
  - Top removed query parameters

## Privacy

- All processing is local on-device
- No ad SDKs
- No user account required
- No telemetry pipeline implemented

## Tech stack

- Kotlin
- Android Jetpack Compose + Material 3
- Room (local database)
- Coroutines + Flow

## Build and run

### Requirements

- Android Studio (recent stable)
- Android SDK 36
- JDK 11+

## Contributing

Issues and PRs are welcome.
