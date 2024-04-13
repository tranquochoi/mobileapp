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

# Hướng dẫn Kết nối Firestore với Android Studio
## Giới thiệu
Firestore, là một dịch vụ cơ sở dữ liệu đám mây của Google, là một giải pháp đa nền tảng và thời gian thực cho việc quản lý và đồng bộ dữ liệu trong ứng dụng di động và website. Được phát triển dựa trên cơ sở hạ tầng mạnh mẽ của Google Cloud, Firestore không chỉ là một cơ sở dữ liệu thông thường mà còn mang lại nhiều tính năng và lợi ích mạnh mẽ.

## 1. Tạo Dự án Firebase

Trước tiên, để kết nối Firestore với Android Studio, bạn cần truy cập vào [trang chủ Firebase](https://firebase.google.com/docs/firestore) và thực hiện các bước sau:

### Bước 1: Tạo Dự án Firebase
- Truy cập trang chủ của Firebase.
- Chọn "Create a project" để tạo một dự án mới.
![Giao diện chính của Android Studio](https://firebasestorage.googleapis.com/v0/b/theryna-fd1d9.appspot.com/o/github%2Ff1.png?alt=media&token=b45abe9c-9c4d-4ae7-a550-72c255447123)

### Bước 2: Thiết lập Dự án
- Đặt tên cho dự án.
- Chấp nhận điều khoản.
- Chọn tài khoản Google Analytics.

### Bước 3: Tạo Dự án mới
- Nhấn "Create project" để hoàn thành quá trình thiết lập.
![Giao diện chính của Android Studio](https://firebasestorage.googleapis.com/v0/b/theryna-fd1d9.appspot.com/o/github%2Ff2.png?alt=media&token=1ec38313-9b08-4573-b83e-7a31c9a19e7a)

## 2. Thêm Ứng dụng Android vào Dự án Firebase

### Bước 1: Đăng ký ứng dụng
- Nhấn vào biểu tượng Android trên màn hình để thêm một "Android App".
- Nhập gói ứng dụng Android.

### Bước 2: Tải xuống và thêm tệp cấu hình
- Tải xuống tệp `google-services.json`.
- Paste tệp vào thư mục "app" của dự án Android.

### Bước 3: Thêm Firebase SDK
- Copy các mục được cung cấp và paste vào 2 tệp `build.gradle.kts`: Project và Module.

## 3. Tạo Cloud Firestore

### Bước 1: Tạo Cloud Firestore
- Chọn mục "Build" và chọn "Firestore Database".
- Nhấn vào "Create Database".

## 4. Kiểm tra Quyền truy cập

- Kiểm tra "Rules" để xem đã cấp quyền "Read" và "Write" chưa. 
- Nếu cần thiết, thiết lập quyền truy cập.

Chúc mừng! Bạn đã kết nối Firestore với Android Studio thành công. Bây giờ bạn có thể thêm các collection và sử dụng nó trong ứng dụng của mình.

