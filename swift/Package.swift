// swift-tools-version:5.1
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "TeaXml",
    products: [
        .library(
            name: "TeaXml",
            targets: ["TeaXml"])
    ],
    dependencies: [
        .package(url: "https://github.com/chenyunguiMilook/SwiftyXML.git", from: "3.0.2")
    ],
    targets: [
        .target(
            name: "TeaXml",
            dependencies: ["SwiftyXML"]),
        .testTarget(
            name: "TeaXmlTests",
            dependencies: ["TeaXml", "SwiftyXML"]),
    ]
)
