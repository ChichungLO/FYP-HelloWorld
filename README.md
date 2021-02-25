# FYP-HelloWorld
OUHK2020 Computing Final Year Project - Hello World Team 

# 仓库架构
### 基于安卓10.0之前

以下项目仅用于初步测试使用，现已停止维护与更新。

【ImagetoPDF】

用于测试将图片转换到PDF的一个测试项目。

【ImgSelecter】

用于测试从相册选择图片并展示在ImageView上的一个测试项目。

【ExportPdf】：

整合了【ImagetoPDF】和【ImgSelecter】，初步实现了拍照、从相册读取图片和导出PDF的功能。导出PDF功能目前只在模拟器上成功运行，真机无法通过测试。

### 基于安卓10.0以后

以下项目都将用于 Final Year Project 的正式开发中，目前采用功能分块的开发模式。即一个功能单独为一个项目，便于各开发人员单独调试与测试。后续会进行整合。

项目的最低运行环境为安卓 10.0 版本（API 29）。

【EditPart】

用于开发，涂抹、手写功能以实现去除手写笔迹。**对应 Objective 3**

【fyp10_2】

用于开发，自动检测和自动清除手写笔记功能。**对应 Objective 1 和 2**

【PDFExport】

用于开发，将处理后的图像导出为PDF格式以便于打印和分享之功能。**对应 Onjective 6**

