# 响应格式化工具


def success_response(data=None, msg="success"):
    """
    成功响应格式化
    :param data: 响应数据
    :param msg: 响应消息
    :return: 格式化后的响应字典
    """
    return {
        "code": 200,
        "msg": msg,
        "data": data
    }


def error_response(code=500, msg="error", data=None):
    """
    错误响应格式化
    :param code: 错误码
    :param msg: 错误消息
    :param data: 额外数据
    :return: 格式化后的响应字典
    """
    return {
        "code": code,
        "msg": msg,
        "data": data
    }


def bad_request_response(msg="Bad Request"):
    """
    400错误响应
    :param msg: 错误消息
    :return: 格式化后的响应字典
    """
    return error_response(400, msg)


def not_found_response(msg="Not Found"):
    """
    404错误响应
    :param msg: 错误消息
    :return: 格式化后的响应字典
    """
    return error_response(404, msg)


def internal_error_response(msg="Internal Server Error"):
    """
    500错误响应
    :param msg: 错误消息
    :return: 格式化后的响应字典
    """
    return error_response(500, msg)