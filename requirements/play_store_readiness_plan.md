# Play Store Readiness Plan

## Goal

Prepare PoppingStar for a first Google Play production release. Do not submit until the release build, app behavior, privacy disclosures, signing, and store listing all meet the exit criteria below.

## Current Status

- [ ] Release build passes lint and packages normally.
- [ ] Release artifact is signed with the production upload key.
- [x] Versioning is set for the first production release.
- [x] Room leaderboard migration is covered by an instrumentation test.
- [x] Corrupted or outdated persisted settings fail safely.
- [ ] Physical-device and lifecycle testing is complete.
- [ ] Privacy policy and Play Console Data safety answers are complete.
- [ ] Store listing assets and content declarations are complete.
- [ ] Release branch is clean and contains only reviewed changes.

## Phase 1: Build and Release Configuration

- [ ] Resolve the `lintVitalRelease` failure involving Android Lint/UAST.
- [ ] Align the Android Gradle Plugin and Gradle wrapper versions, then verify with a clean build.
- [ ] Run the normal release tasks without excluding lint:

  ```bash
  ./gradlew clean :app:lintVitalRelease :app:bundleRelease
  ```

- [ ] Configure release signing through protected Gradle properties or Android Studio signing configuration.
- [ ] Never commit the keystore, passwords, or signing credentials.
- [ ] Generate and archive the signed AAB outside the repository.
- [ ] Confirm the final artifact with Play Console's pre-launch checks or bundle validation.
- [ ] Set a production-ready `versionCode` and `versionName`; increment `versionCode` for every later upload.

## Phase 2: Reliability and Data Safety

- [x] Add a Room migration test from database version 1 to version 2.
- [x] Verify duplicate leaderboard rows collapse to one row per profile and retain the highest score.
- [x] Verify a lower later score cannot replace a player's best score.
- [x] Handle invalid `ThemeMode`, `OrientationMode`, and `BackgroundMode` values by falling back to defaults instead of crashing.
- [ ] Test missing, invalid, or unavailable background image URIs.
- [ ] Test process death, app backgrounding, rotation, orientation changes, and window resizing.
- [x] Implement profile deletion to clear the profile, leaderboard score, current profile ID, and saved session data.
- [x] Implement complete local-data clearing to return to the root flow.
- [ ] Test backup and restore behavior for profiles, settings, scores, and image references.
- [ ] Run unit tests and instrumented tests on at least one physical device and one emulator API level supported by the app.

## Phase 3: Play Policy and Privacy

- [ ] Publish a privacy policy URL that accurately describes local profile names, scores, settings, image references, audio, backups, and whether any data leaves the device.
- [ ] Complete the Play Console Data safety form consistently with the privacy policy and actual code.
- [ ] Complete the content rating questionnaire.
- [ ] Declare the target audience and confirm whether the app is directed to children.
- [ ] Review ads, analytics, crash reporting, login, and third-party SDK declarations; remove any declaration that is not applicable.
- [ ] Confirm that the app requests no unnecessary permissions and that photo selection uses the Android Photo Picker where applicable.
- [ ] Review backup behavior so private local profile data is handled as intended.

## Phase 4: Store Listing and Product QA

- [ ] Finalize the product name, icon, short description, full description, and category.
- [ ] Provide required phone screenshots and a feature graphic where applicable.
- [ ] Verify launcher icons and adaptive icons on current Android versions.
- [ ] Test first launch, profile creation, gameplay, pause/resume, sound, themes, backgrounds, leaderboard, and destructive data actions.
- [ ] Check accessibility: touch targets, content descriptions, contrast, text scaling, keyboard/focus behavior, and landscape layouts.
- [ ] Test offline behavior and confirm the app remains usable without network access.
- [ ] Confirm the app has no debug menus, test data, placeholder text, or development endpoints.
- [ ] Review the final app name consistently across the launcher, Play listing, README, and screenshots.

## Phase 5: Release Candidate and Submission

- [ ] Create a clean release branch from the reviewed main branch.
- [ ] Confirm `git status` is clean before building the release candidate.
- [ ] Run the complete verification suite:

  ```bash
  ./gradlew clean test lintVitalRelease bundleRelease
  ```

- [ ] Upload the signed AAB to an internal Play testing track.
- [ ] Install from Play testing on physical devices and complete a smoke test.
- [ ] Review pre-launch report results and fix all crashes and high-priority warnings.
- [ ] Confirm staged rollout, support contact, app access instructions, and release notes.
- [ ] Submit to production only after all checklist items are complete.

## Release Exit Criteria

The app is ready for production only when:

1. The normal release lint and bundle tasks pass without exclusions.
2. The uploaded AAB is signed with the intended production key.
3. No open high-severity crash, data-loss, privacy, or install issue remains.
4. Room migrations and destructive data operations are verified on real app data.
5. Play Console policy, Data safety, content, audience, and store-listing requirements are complete.
6. Internal testing passes on representative physical devices.
7. The release branch is clean and the exact tested commit is the one uploaded.
