# Mobile Programing Term Project

## Overview
- It is a term project for the year 3 module, Mobile Programming using Android Studio.

- Application Name : **SIKJIPSA**; SIK(First syllable of plant in Korean) + JIPSA(A butler in Korean)

- Summary of our application : An online community for those who love to have plants.

### 1. Background

<img width="600" alt="Screenshot 2022-12-09 at 2 27 55 PM" src="https://user-images.githubusercontent.com/108987773/206630849-328dd564-b9c3-4325-99c1-f9ded4e7d3d7.png">

- The culture of raising companion plants is in the spotlight.
- Growing plants has emerged as a popular hobby since COVID-19, and sales in related industries are also increasing rapidly.

### 2. Main Functions

<img width="300" alt="Screenshot 2022-12-09 at 9 27 41 PM" src="https://user-images.githubusercontent.com/108987773/206702457-f1e08f44-8bd9-40b5-b1f4-22fcfa9c73b7.png"><img width="300" alt="Screenshot 2022-12-09 at 9 27 57 PM" src="https://user-images.githubusercontent.com/108987773/206702489-2896faf8-ca1f-4370-8dfa-a04f295e8a27.png">

- [Uploading a post][link6], [liking the post][link13], and [leaving comments][link12] for a communication with other users.

<img width="300" alt="Screenshot 2022-12-09 at 9 29 18 PM" src="https://user-images.githubusercontent.com/108987773/206702731-91924253-3f5d-4c9a-be97-181a9b3efc21.png">

- [Searching a name of unknown plants through image or keyword.][link5]

<img width="300" alt="Screenshot 2022-12-09 at 9 29 58 PM" src="https://user-images.githubusercontent.com/108987773/206702839-29be50fd-bede-4101-8d19-5b57b3162fa6.png">

- [Checking weather][link8]

<img width="300" alt="Screenshot 2022-12-09 at 9 30 28 PM" src="https://user-images.githubusercontent.com/108987773/206702913-25cd11f4-5a16-4ca7-854d-8caf7cdf6d81.png"><img width="300" alt="KakaoTalk_Photo_2022-12-09-21-31-23" src="https://user-images.githubusercontent.com/108987773/206703096-8ea3105c-7957-4f5f-b816-dba39d84c5ce.png">

- [Setting alarm for watering][link9]

<img width="300" alt="Screenshot 2022-12-09 at 9 32 47 PM" src="https://user-images.githubusercontent.com/108987773/206703265-fdcaee49-bbed-4add-9c5e-775b5a606715.png"><img width="300" alt="Screenshot 2022-12-09 at 9 33 19 PM" src="https://user-images.githubusercontent.com/108987773/206703361-147a00c4-8a3a-4ee9-b0b8-a87787d66111.png">

- [Changing user information][link10]

### 3. Components
- Functions
  - [Login][link] : Two way of login, authentication through Firebase or using google account
  - [Logout][link2] : Log-out pressing logout button
  - [Register][link3] : Registering an account
  - [Main page][link4] : Showing all posts and user information
  - [Search][link5] : Importing [tflite model][link14] trained. Searching about plants through image or keyword and the result can be shown either through in-app or connection to google.
  - [Writing post][link6] : Adding image and contents
  - [FCM][link7] : Receiving a push alarm
  - [Weather][link8] : Showing weather information
  - [Alarm for watering][link9] : Receiving alarm for watering
  - [Edit my profile][link10] : Changing personal information
  - [My post][link11] : Seeing posts I have written
  - [Comment][link12] : Leaving comments on a post
  
  
  [link]: https://github.com/jeewonkimm2/SIKJIPSA/blob/master/app/src/main/java/com/example/sikjipsa/LoginActivity.kt
  [link2]: https://github.com/jeewonkimm2/SIKJIPSA/blob/master/app/src/main/java/com/example/sikjipsa/navigation/UserFragment.kt
  [link3]: https://github.com/jeewonkimm2/SIKJIPSA/blob/master/app/src/main/java/com/example/sikjipsa/SignUpActivity.kt
  [link4]: https://github.com/jeewonkimm2/SIKJIPSA/blob/master/app/src/main/java/com/example/sikjipsa/MainActivity.kt
  [link5]: https://github.com/jeewonkimm2/SIKJIPSA/blob/master/app/src/main/java/com/example/sikjipsa/SearchActivity.kt
  [link6]: https://github.com/jeewonkimm2/SIKJIPSA/blob/master/app/src/main/java/com/example/sikjipsa/navigation/AddPhotoActivity.kt
  [link7]: https://github.com/jeewonkimm2/SIKJIPSA/blob/master/app/src/main/java/com/example/sikjipsa/MyFirebaseMessagingService.kt
  [link8]: https://github.com/jeewonkimm2/SIKJIPSA/blob/master/app/src/main/java/com/example/sikjipsa/MainActivity.kt
  [link9]: https://github.com/jeewonkimm2/SIKJIPSA/blob/master/app/src/main/java/com/example/sikjipsa/WateringActivity.kt
  [link10]: https://github.com/jeewonkimm2/SIKJIPSA/blob/master/app/src/main/java/com/example/sikjipsa/navigation/UserFragment.kt
  [link11]: https://github.com/jeewonkimm2/SIKJIPSA/blob/master/app/src/main/java/com/example/sikjipsa/MyPostActivity.kt
  [link12]: https://github.com/jeewonkimm2/SIKJIPSA/blob/master/app/src/main/java/com/example/sikjipsa/CommentActivity.kt
  [link13]: https://github.com/jeewonkimm2/SIKJIPSA/blob/master/app/src/main/java/com/example/sikjipsa/navigation/DetailViewFragment.kt
  [link14]: https://github.com/jeewonkimm2/SIKJIPSA/blob/master/model_fp16.tflite
  [link15]: https://github.com/jeewonkimm2/SIKJIPSA/blob/master/FlowerClassifier.ipynb
  
- UI/UX
  - Navigation bar
  - Fragment
  - RecyclerView
  - LinearLayout
  - RelativeLayout
  - FrameLayout
  - View
  
### 4. Limitation
- Due to hardware performance, for the [image classification training][link15], training various kinds of images was difficult.




#### reference

- https://www.nongsaro.go.kr/portal/ps/psv/psvr/psvre/curationDtl.ps?menuId=PS03352&srchCurationNo=1696
