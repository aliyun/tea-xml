file(GLOB_RECURSE coverage_data "${COVERAGE_BUILD_DIR}/*.gcda")
if(coverage_data)
    file(REMOVE ${coverage_data})
endif()
