#
# CMakeLists.txt
#
# Build of the Google Test external project
#
# Taken (almost) entirely from the guide here:
#     https://github.com/google/googletest/blob/master/googletest/README.md#incorporating-into-an-existing-cmake-project
#

cmake_minimum_required(VERSION 3.11 FATAL_ERROR)

# Clone and build base directory
set(GOOGLE_TEST_DIR ${CMAKE_CURRENT_SOURCE_DIR})
find_package(GTest QUIET)

if(GTest_FOUND)
  add_library(gtest_main INTERFACE)
  target_link_libraries(gtest_main INTERFACE GTest::gtest_main)
else()
  include(FetchContent)
  FetchContent_Declare(
    googletest
    GIT_REPOSITORY https://github.com/google/googletest.git
    GIT_TAG        release-1.8.0
  )
  FetchContent_GetProperties(googletest)

  if(NOT googletest_POPULATED)
    FetchContent_Populate(googletest)
    # Prevent GoogleTest from overriding our compiler/linker options
    # when building with Visual Studio
    set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
    # Prevent GoogleTest from using PThreads
    set(gtest_disable_pthreads ON CACHE BOOL "" FORCE)

    # adds the targers: gtest, gtest_main, gmock, gmock_main
    add_subdirectory(
      ${googletest_SOURCE_DIR}
      ${googletest_BINARY_DIR}
      )

    # Silence std::tr1 warning on MSVC
    if(MSVC)
      foreach(_tgt gtest gtest_main gmock gmock_main)
        target_compile_definitions(${_tgt}
          PRIVATE
            "_SILENCE_TR1_NAMESPACE_DEPRECATION_WARNING"
          )
      endforeach()
    endif()
  endif()
endif()

