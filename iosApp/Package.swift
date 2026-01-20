// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "iosApp",
    platforms: [
        .iOS(.v17)
    ],
    products: [
        .library(
            name: "iosApp",
            targets: ["iosApp"]
        )
    ],
    dependencies: [
        // Firebase iOS SDK
        .package(
            url: "https://github.com/firebase/firebase-ios-sdk.git",
            from: "10.20.0"
        )
    ],
    targets: [
        .target(
            name: "iosApp",
            dependencies: [
                .product(name: "FirebaseAuth", package: "firebase-ios-sdk"),
                .product(name: "FirebaseFirestore", package: "firebase-ios-sdk"),
                .product(name: "FirebaseDatabase", package: "firebase-ios-sdk"),
                .product(name: "FirebaseStorage", package: "firebase-ios-sdk"),
                .product(name: "FirebaseAnalytics", package: "firebase-ios-sdk")
            ],
            path: "iosApp"
        )
    ]
)
