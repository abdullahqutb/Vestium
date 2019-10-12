
Group Name: Semicolons - G2D
Members: Muhammad Arham Khan, Amirhossein Maghsoudi, Sarah
Javaid, Sayed Abdullah Qutb, Mohamad Fakhouri, Esra Nur
Deniz, Ahmet Berk Eren


Project Title: Vestium
Project Description: "Vestium" (Latin for 'wardrobe') is a smart wardrobe organizer application that assists humans
in knowing what clothes they own, intelligently suggests dress options daily and allows connectivity
among friend circles, hence revolutionizing the bland dress-selection process. Taking
advantage of the android platform and the cloud-based data storage �firebase', Vestium allows
the users to organize, manage and access their fashion anytime, anywhere.

Things done so far (Current status):
*Basically, pretty much everything we promised, works!
-Backend parcelable/ Serializable classes implemented
-Firebase implementation done
-User Interface designed/ implemented
-Add item/ Wardrobe/ Calendar/ Shopping/ Looks/ Circles modules have been implemented
-Error handling done
-Data latency issues solved
-Static data Caching done
-Image Compression while adding items done
-Integration testing done
-Todays look methods implemented and tested


Who did what (Roughly)?
-Arham: Firebase implementation/ UI Design/ ViewModel class code
-Mohamad: Today's Look/ Backend model class code
-Ameer: Calendar module and backend model class code
-Esra: Style trends module and backend model class code
-Abdullah: Todays look module
-Sarah: Wardrobe module
-Ahmet: Shopping module

Organization:
-All backend model classes are stored in allinontech.vestium.backend directory
-All viewModel classes are stored in project's root, allinontech.vestium
-All custom UI elements are stored in allinontech.vestium
-All design xml files are stored in allinontech.vestium.res.layout
-All dependencies of the project may be found in build.gradle(app) file
-The projects root may be found at, SemicolonsVestium\app\src

Tools/ Libraries:
-Vestium was coded in Android Studio 3.1 and uses the following libraries and dependencies:
    'com.android.support:appcompat-v7:27.1.1'
    'com.android.support.constraint:constraint-layout:1.0.2'
    'com.google.firebase:firebase-auth:15.0.0'

    'com.android.support:support-v4:27.1.0'
    'com.google.firebase:firebase-database:15.0.0'
    'com.google.firebase:firebase-storage:15.0.0'
    'com.google.android.gms:play-services-auth:15.0.0'
    'junit:junit:4.12'
    'com.android.support.test:runner:1.0.1'
    'com.android.support.test.espresso:espresso-core:3.0.1'
    'com.android.support:design:27.1.1'
    'com.android.support:cardview-v7:27.1.1'
    'com.jakewharton:butterknife:8.6.0'
    'com.github.bumptech.glide:glide:4.7.1'
    'com.github.bumptech.glide:compiler:4.7.1'
    'com.google.firebase:firebase-core:15.0.2'
    'com.github.chrisbanes:PhotoView:2.1.3'
    'com.android.support:palette-v7:27.1.1'
    'com.firebaseui:firebase-ui-database:3.3.1'
    'com.github.florent37:materialtextfield:1.0.7'
    'com.android.support:recyclerview-v7:27.1.1'
    'com.android.support:cardview-v7:27.1.1'


How to recompile:
-Install Android Studio 3.1 and Java SDK Manager
-Unzip this file
-Double click on the SemicolonsVestium item with the android studio icon
-When opened, go to Build>Clean Build
-Connect an android device or install and emulator, Click on run in the build menu, and run the application on the device.


