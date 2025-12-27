# 响应格式化工具


def success(data=None, msg="success"):
    """
    成功响应格式
    :param data: 响应数据
    :param msg: 响应消息
    :return: 格式化的响应字典
    """
    return {
        "code": 200,
        "msg": msg,
        "data": data
    }


def error(code=500, msg="error", data=None):
    """
    错误响应格式
    :param code: 错误代码
    :param msg: 错误消息
    :param data: 错误数据
    :return: 格式化的响应字典
    """
    return {
        "code": code,
        "msg": msg,
        "data": data
    }

# 与学习路径推荐模块保持一致的函数名
success_response = success
error_response = error


def bad_request(msg="Bad Request"):
    """
    400错误响应
    :param msg: 错误消息
    :return: 格式化的响应字典
    """
    return error(400, msg)


def not_found(msg="Not Found"):
    """
    404错误响应
    :param msg: 错误消息
    :return: 格式化的响应字典
    """
    return error(404, msg)


def server_error(msg="Internal Server Error"):
    """
    500错误响应
    :param msg: 错误消息
    :return: 格式化的响应字典
    """
    return error(500, msg)


def format_enterprise_recommendation(enterprises):
    """
    格式化企业推荐结果
    :param enterprises: 企业推荐数据列表
    :return: 格式化的企业推荐结果
    """
    formatted = []
    for enterprise in enterprises:
        formatted_enterprise = {
            "enterpriseId": enterprise.get("enterpriseId"),
            "enterpriseName": enterprise.get("enterpriseName"),
            "relevanceScore": enterprise.get("relevanceScore", 0),
            "matchedTags": enterprise.get("matchedTags", []),
            "positions": enterprise.get("positions", [])
        }
        formatted.append(formatted_enterprise)
    return formatted


def format_position_data(positions):
    """
    格式化职位数据
    :param positions: 职位数据列表
    :return: 格式化的职位数据
    """
    formatted = []
    for position in positions:
        formatted_position = {
            "positionId": position.get("positionId"),
            "positionName": position.get("positionName"),
            "salary": position.get("salary"),
            "city": position.get("city")
        }
        formatted.append(formatted_position)
    return formatted