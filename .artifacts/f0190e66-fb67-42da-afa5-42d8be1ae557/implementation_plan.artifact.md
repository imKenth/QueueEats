# Layout Folder Organization Plan

Organize the `res/layout` folder by standardizing naming conventions and removing unsupported subfolders. This will improve maintainability and make it easier to find files.

## User Review Required

> [!IMPORTANT]
> This will involve renaming many layout files. I will automatically update references in all Kotlin activities and XML `include` tags, but if you have hardcoded layout strings elsewhere, they might need manual updating.

## Proposed Changes

### [res/layout] folder

I will rename and move files to follow a strict `[type]_[description].xml` pattern.

#### [MOVE & RENAME] [splashscreen.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/test%20folder/splashscreen.xml) -> [activity_splash.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/activity_splash.xml)
- Move out of `test folder/` to the root `layout/` directory.
- Rename to follow `activity_` prefix for clarity.

#### [RENAME] [signinscreen.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/signinscreen.xml) -> [activity_sign_in.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/activity_sign_in.xml)
- Used by `SignInActivity` and `MainActivity`.

#### [RENAME] [signupscreen.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/signupscreen.xml) -> [activity_sign_up.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/activity_sign_up.xml)
- Used by `SignupActivity`.

#### [RENAME] [activity_main.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/activity_main.xml) -> [activity_dashboard.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/activity_dashboard.xml)
- Used by `DashboardActivity`. Renaming to match the activity name.

#### [RENAME] [branch1.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/branch1.xml) -> [activity_branch_details.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/activity_branch_details.xml)
- Used by `Branch1Activity`.

#### [RENAME] [navbar.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/navbar.xml) -> [layout_navbar.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/layout_navbar.xml)
- Follows `layout_` prefix for reusable components.

#### [RENAME] [promotional_banner.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/promotional_banner.xml) -> [layout_promotional_banner.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/layout_promotional_banner.xml)

#### [RENAME] [branches.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/branches.xml) -> [layout_branches_list.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/layout_branches_list.xml)

#### [RENAME] [poblancion_branch.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/poblancion_branch.xml) -> [item_branch_poblacion.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/item_branch_poblacion.xml)

#### [RENAME] [calinan_branch.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/calinan_branch.xml) -> [item_branch_calinan.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/item_branch_calinan.xml)

#### [RENAME] [item_menu_dish.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/item_menu_dish.xml) -> [item_menu_dish_adobo.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/item_menu_dish_adobo.xml)
#### [RENAME] [item_menu_dish1.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/item_menu_dish1.xml) -> [item_menu_dish_sinigang.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/item_menu_dish_sinigang.xml)
#### [RENAME] [item_menu_dish2.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/item_menu_dish2.xml) -> [item_menu_dish_lechon.xml](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/item_menu_dish_lechon.xml)

#### [DELETE] [test folder/](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/res/layout/test%20folder/)
- Remove the non-standard subdirectory after moving its content.

### [src/main/java/com/example/queueeats] code updates

I will update the `setContentView(R.layout...)` calls in:
- [splashScreen.kt](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/java/com/example/queueeats/splashScreen.kt)
- [SignInActivity.kt](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/java/com/example/queueeats/SignInActivity.kt)
- [SignupActivity.kt](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/java/com/example/queueeats/SignupActivity.kt)
- [DashboardActivity.kt](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/java/com/example/queueeats/DashboardActivity.kt)
- [Branch1Activity.kt](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/java/com/example/queueeats/Branch1Activity.kt)
- [MainActivity.kt](file:///home/rai/AndroidStudioProjects/QueueEats/app/src/main/java/com/example/queueeats/MainActivity.kt)

## Verification Plan

### Automated Tests
- I will run a project build to ensure all layout references are valid: `./gradlew :app:assembleDebug`

### Manual Verification
- Verify that the IDE no longer shows errors in the Kotlin files or XML `include` tags.
