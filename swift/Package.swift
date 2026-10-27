// swift-tools-version:5.6
// The swift-tools-version declares the minimum version of Swift required to build this package.
import PackageDescription

let package = Package(
        name: "DarabonbaXML",
        platforms: [.macOS(.v10_15),
                    .iOS(.v13),
                    .tvOS(.v13),
                    .watchOS(.v6)],
        products: [
            .library(
                    name: "DarabonbaXML",
                    targets: ["DarabonbaXML"])
        ],
        dependencies: [
            // Dependencies declare other packages that this package depends on.
            .package(url: "https://github.com/drmohundro/SWXMLHash.git", from: "7.0.2"),
        ],
        targets: [
            .target(
                    name: "DarabonbaXML",
                    dependencies: [
                        .product(name: "SWXMLHash", package: "SWXMLHash"),
                    ]),
            .testTarget(
                    name: "DarabonbaXMLTests",
                    dependencies: [
                        "DarabonbaXML",
                    ]),
        ],
        swiftLanguageVersions: [.v5]
)
