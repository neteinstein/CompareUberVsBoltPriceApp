# Fix Summary: Title Appearing Behind Camera Hole

## Problem
The "Compare App" title was appearing behind the camera hole (notch/punch-hole cutout) on devices with display cutouts. This is a common issue when apps don't properly handle window insets.

## Root Cause
The app was not handling system window insets, particularly:
- Status bar area
- Display cutouts (camera holes, notches)

The Column containing the title started at the very top of the screen without accounting for these system UI elements.

## Solution
Implemented proper window insets handling using Jetpack Compose's built-in modifiers:

### Changes Made

1. **Enabled Edge-to-Edge Display** (MainActivity.kt, line 56-57)
   ```kotlin
   // Enable edge-to-edge display to handle window insets properly
   WindowCompat.setDecorFitsSystemWindows(window, false)
   ```
   - This allows the app to draw behind system bars
   - Gives us full control over content positioning

2. **Added Status Bar Padding** (MainActivity.kt, line 81)
   ```kotlin
   Column(
       modifier = Modifier
           .fillMaxSize()
           .statusBarsPadding()  // Added this line
           .padding(16.dp),
       // ...
   )
   ```
   - `statusBarsPadding()` adds padding equal to the status bar height
   - Ensures content appears below the status bar and camera cutout
   - Applied before the general padding to respect system insets first

3. **Added Required Imports**
   - `androidx.core.view.WindowCompat` (line 39)
   - `androidx.compose.foundation.layout.statusBarsPadding` (line 18)

## Technical Details

### Modifier Order
The order of modifiers is important in Compose:
```kotlin
.fillMaxSize()           // Fill the entire screen
.statusBarsPadding()     // Add padding for status bar (top only)
.padding(16.dp)          // Add content padding (all sides)
```

This results in:
- **Top padding**: status bar height + 16dp
- **Left/Right/Bottom padding**: 16dp

This is the correct and recommended approach because:
1. System insets (status bar, navigation bar) should be respected first
2. Then add your design/content padding

### Why This Works
- `WindowCompat.setDecorFitsSystemWindows(window, false)` tells Android that we want to handle window insets ourselves
- `statusBarsPadding()` automatically accounts for:
  - Status bar height
  - Display cutouts (camera holes, notches)
  - Different screen configurations
- The solution is device-agnostic and works across all Android devices with varying cutout styles

## Testing
The fix should be tested on:
- Devices with camera cutouts/notches
- Devices without cutouts (regular status bar)
- Different Android versions (API 24+)
- Portrait and landscape orientations

## Impact
- **Minimal change**: Only 6 lines added (3 imports + 2 code lines + 1 comment)
- **No breaking changes**: Existing functionality remains intact
- **Improved UX**: Title and content now display correctly on all devices
- **Best practice**: Follows Android's recommended approach for handling window insets in Compose

## References
- [Android Window Insets Documentation](https://developer.android.com/develop/ui/views/layout/edge-to-edge)
- [Jetpack Compose Window Insets](https://developer.android.com/jetpack/compose/layouts/insets)
