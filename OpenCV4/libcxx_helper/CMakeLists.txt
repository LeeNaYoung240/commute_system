cmake_minimum_required(VERSION 3.6)
set(pathLIBOPENCV_JAVA Test\\appsrc\\main\\jnilibs\\armeabi-v7a\\libopencv_java4.so)
# dummy target to bring libc++_shared.so into packages
add_library(opencv_jni_shared STATIC dummy.cpp)
