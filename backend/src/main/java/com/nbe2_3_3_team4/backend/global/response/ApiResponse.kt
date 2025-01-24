package com.nbe2_3_3_team4.backend.global.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.nbe2_3_3_team4.backend.global.constant.ResponseStatus

/**
 * 데이터 공통 반환 형식 정의 클래스
 *
 * @param <T>
</T> */
class ApiResponse<T> (val status: String, val message: String?, @field:JsonInclude(JsonInclude.Include.NON_NULL) val data: T? ){


    companion object {
        // 반환 데이터 없는 성공 response
        fun createSuccessWithNoData(): ApiResponse<Any> {
            return ApiResponse( status = ResponseStatus.SUCCESS.msg, message = null, data =null)
        }

        // 반환 데이터 있는 성공 response
        fun <T> createSuccess(data: T): ApiResponse<T> {
            return ApiResponse( status = ResponseStatus.SUCCESS.msg, message = null, data = data)
        }

        // 에러 response
        fun createError(msg: String?): ApiResponse<Any> {
            return ApiResponse( status = ResponseStatus.ERROR.msg, message = msg, data =null)
        }

        // 에러 response(msg 직접 지정)
        fun createErrorWithMsg(msg: String?): ApiResponse<Any> {
            return ApiResponse( status = ResponseStatus.ERROR.msg, message = msg, data =null)
        }
    }
}

