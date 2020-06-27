# Child bicycle Tracking System

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

Child Bicycle Tracking System is IOT solution which let parent to monitor their children while they are playing with their bicycle. This system consists of a hardware component which can be plugin to child bicycle and android application. With using this system parents can get following smart features. This Android application can be extended to your purposes. You can contribute to this system as well.

- [x] Identify whether bicycle is using or not.
    This is done by the pressure sensor installed in the bicycle seat. From this we can detect
- [x] Indicate whether the bicycle is fallen.
    This is detected from tilt sensor. If bicycle is fallen immediately a notification goes to the parent.
- [x] Track Bicycle Speed.
    Speed is calculated using the GPS coordinates and it is displayed real-time in the android application. Not only that, maximum speed can be defined and if child exceeds that both child and parent will be alerted.
[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)
- [x] Identify exactly where the child is playing to ensure the safety.
    Child playing location is displayed in a map in the application. It is also dynamically updated when child is moving. GPS locator is used to get the location.
[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)
- [x] Set up safe zones and receive alerts when children go beyond them.
    Parent get the privilege to draw regions in the map and from that can monitor whether child go beyond them through application.
- [x] Automatic call feature by button press.
    When any incident happen, child can call the parent by pressing a button in the bicycle. Speaker and a mic also plugged to the GSM module to do this. This is an extra feature
expected to be added.

### Tech

Dillinger uses a number of open source projects to work properly:

* Android
* Firebase
* Aurdino

### Development

Want to contribute? Great!

You need to create your own firebase project and replace the `google-services.json`
Create a databse in firebase as shown in the picture.
[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)
Now you are good to go.

Here is how to test your app with only the Firebase.
[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

If you wanted to work with the hardware and with the live data products to test your product you need to setup Ardino and Ardino firbase library.


