# Hướng dẫn cài đặt và sử dụng Android Studio

## Giới thiệu

Android Studio là môi trường phát triển tích hợp (IDE) chính thức được phát triển bởi Google để phát triển ứng dụng di động trên nền tảng Android. 

## Cài đặt Android Studio

Để cài đặt Android Studio, làm theo các bước sau:

1. **Truy cập trang chủ của Android Studio:**
   - Truy cập trang website chính thức của Android Studio tại [đây](https://developer.android.com/studio).

2. **Tải và cài đặt:**
   - Tại trang chủ, tìm đến mục "Download" để tải IDE về máy tính.
   - Sau khi tải xong, chạy tệp cài đặt và tuân thủ các hướng dẫn trên màn hình để hoàn tất quá trình cài đặt.

3. **Cấu hình Android Studio:**
   - Khởi động Android Studio sau khi cài đặt hoàn tất.
   - Theo các hướng dẫn trên màn hình để cấu hình các thiết lập ban đầu, bao gồm cài đặt JDK và SDK Android.

## Tạo dự án mới trong Android Studio

Sau khi đã cài đặt thành công, bạn có thể bắt đầu tạo dự án mới trong Android Studio bằng cách làm theo các bước sau:

1. **Mở Android Studio:**
   - Mở Android Studio sau khi đã cài đặt thành công.
2. **Tạo dự án mới:**
   - Trong giao diện chính của Android Studio, nhấn vào "New Project" để bắt đầu tạo dự án mới.
![Giao diện chính của Android Studio](https://firebasestorage.googleapis.com/v0/b/theryna-fd1d9.appspot.com/o/github%2Fgdc.png?alt=media&token=c09e20af-0e9d-413c-a800-d91caa91bd4f)
3. **Chọn loại Activity:**
   - Chọn loại Activity phù hợp với dự án của bạn. Ví dụ, bạn có thể chọn "Empty Activity" để bắt đầu với một dự án trống.
![ạo dự án Android Studio](https://firebasestorage.googleapis.com/v0/b/theryna-fd1d9.appspot.com/o/github%2Fas.png?alt=media&token=226a8707-05cd-4b2e-8315-1f350758fa4b)
4. **Thiết lập cài đặt dự án:**
   - Đặt tên cho dự án trong phần "Name".
   - Trong phần "Package name", nhập tên gói, một chuỗi định danh duy nhất để xác định ứng dụng của bạn.
   - Chọn vị trí lưu trữ cho dự án trong phần "Save location".
   - Đối với phần "Minimum SDK", bạn có thể để mặc định hoặc chọn phiên bản tối thiểu của hệ điều hành Android mà ứng dụng yêu cầu để chạy.
   - Cuối cùng, nhấn "Finish" để tạo dự án mới và bắt đầu làm việc.
![ạo dự án Android Studio](https://firebasestorage.googleapis.com/v0/b/theryna-fd1d9.appspot.com/o/github%2Fpic.png?alt=media&token=739e85da-42ba-4600-8146-95607dc15802)

## [Chuyển đến trang Firestore](#firestore)

# Hướng dẫn chuyển từ Android Studio sang Firestore

## Giới thiệu

Firestore là một cơ sở dữ liệu NoSQL linh hoạt và mạnh mẽ của Google, được tích hợp tốt với các ứng dụng Android. Dưới đây là hướng dẫn cách chuyển từ môi trường phát triển Android Studio sang sử dụng Firestore để lưu trữ và quản lý dữ liệu của ứng dụng của bạn.

## Thêm Firestore vào dự án Android

1. **Thêm Firestore SDK:**
   - Mở file `build.gradle` (Module: app) của dự án Android Studio của bạn.
   - Thêm dependencies cho Firestore:
     ```gradle
     implementation 'com.google.firebase:firebase-firestore:23.0.0'
     ```

2. **Kích hoạt Firestore trong Firebase:**
   - Truy cập vào [Firebase Console](https://console.firebase.google.com/).
   - Chọn dự án của bạn hoặc tạo một dự án mới.
   - Trong tab "Develop", chọn "Firestore Database".
   - Nhấn nút "Create database" để tạo một cơ sở dữ liệu Firestore mới.

## Sử dụng Firestore trong ứng dụng của bạn

1. **Tạo, đọc, cập nhật và xóa dữ liệu:**
   - Sử dụng API Firestore để thực hiện các thao tác CRUD (Tạo, Đọc, Cập nhật, Xóa) trên dữ liệu.
   - Tham khảo [tài liệu Firestore](https://firebase.google.com/docs/firestore) để biết thêm chi tiết về cách sử dụng Firestore trong ứng dụng Android của bạn.

2. **Xác thực người dùng:**
   - Để bảo vệ dữ liệu của bạn, sử dụng Firebase Authentication để xác thực người dùng.
   - Tham khảo [tài liệu Firebase Authentication](https://firebase.google.com/docs/auth) để biết thêm chi tiết về cách xác thực người dùng trong ứng dụng của bạn.

## Ví dụ về sử dụng Firestore trong Android

Dưới đây là một ví dụ đơn giản về cách thêm dữ liệu vào Firestore trong ứng dụng Android của bạn:

```java
// Lấy tham chiếu tới Firestore
FirebaseFirestore db = FirebaseFirestore.getInstance();

// Tạo một bản ghi mới
Map<String, Object> data = new HashMap<>();
data.put("name", "John Doe");
data.put("age", 30);

// Thêm bản ghi vào Firestore
db.collection("users").document("user1").set(data)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                // Xử lý thành công, ví dụ: hiển thị thông báo
                Toast.makeText(MainActivity.this, "Dữ liệu đã được thêm vào Firestore!", Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
                // Xử lý lỗi, ví dụ: hiển thị thông báo lỗi
                Toast.makeText(MainActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
