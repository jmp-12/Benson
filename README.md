Benson
=================

Benson (formerly Jenkins) is a voice recognition program designed for the [Udoo Quad](http://shop.udoo.org/usa/?___from_store=usa&popup=no) running Android. Benson can respond to user input with pre-programmed phrases and search the web via Wolfram API for a response to an unknown query. Pre-programmed responses utilize moods to personify Benson's responses as well as accept parameters for serial communication with the onboard DUE. The result is a sarcastic, personable voice recognition system with access to net-based knowledge and Arduino compatible electronics.

####Check out a quick demonstration on [YouTube](http://youtu.be/HjJCI1Hjb2c).

<p align="center">
  <img src="http://i1016.photobucket.com/albums/af284/Turbopwned/bwd6j.gif" alt="Gif"/>
</p>



Using this application
=========
**Please remember this project is an early alpha version, bugs will be plentiful.**<br> 
<br>
In order to use this application on your Udoo Quad, you must do the following.

1. Upload the Arduino [sketch](https://github.com/JohnPersano/Benson/blob/master/arduino/sketches/simple_sketch.ino) to the onboard DUE. 
2. Sign up for an app key from [Wolfram Alpha](https://developer.wolframalpha.com/portal/signin.html) and place it in the [strings resource](https://github.com/JohnPersano/Benson/blob/master/app/src/main/res/values/strings.xml) file.
3. Install GAPPS on your Udoo. See Squawk003's method [here](http://www.udoo.org/forum/install-google-apps-t327-20.html).


Libraries used
=========
[CMUSphinx](http://cmusphinx.sourceforge.net/wiki/tutorialandroid) <br>
[Wolfram Alpha Java bindings](http://products.wolframalpha.com/api/libraries.html) <br>
[Android Visualizer](https://github.com/felixpalmer/android-visualizer) <br>


Developer
=========
[John Persano](https://plus.google.com/+JohnPersano)


License
=======

    Copyright 2014 John Persano

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

