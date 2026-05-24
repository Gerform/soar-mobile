package tech.soc.soar.shared.data.response.mapper

import tech.soc.soar.shared.data.response.dto.CreateResponseResultDto
import tech.soc.soar.shared.domain.response.model.CreateResponseResult

fun CreateResponseResultDto.toDomain(): CreateResponseResult {
    return CreateResponseResult(
        status = status
    )
}