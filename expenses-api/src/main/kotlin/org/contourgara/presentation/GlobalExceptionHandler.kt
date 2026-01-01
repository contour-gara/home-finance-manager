package org.contourgara.presentation

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.util.logging.error
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import org.contourgara.domain.ValidationException

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureGlobalExceptionHandler() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.application.environment.log.error(cause)
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = cause.toResponse(),
            )
        }

        exception<BadRequestException> { call, cause ->
            call.application.environment.log.error(cause)
            when (val e = cause.cause?.cause) {
                is MissingFieldException -> {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = e.toResponse(),
                    )
                }
                is SerializationException -> {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = e.toResponse(),
                    )
                }
                else -> {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = cause.toResponse(),
                    )
                }
            }
        }

        exception<ValidationException> { call, cause ->
            call.application.environment.log.error(cause)
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ErrorResponse(
                    type = cause::class.java.name,
                    title = cause.title,
                    errors = cause.errors.map { ErrorDetail(detail = it) },
                ),
            )
        }
    }
}

@Serializable
private data class ErrorResponse(
    private val type: String,
    private val title: String,
    private val errors: List<ErrorDetail>,
)

@Serializable
private data class ErrorDetail(
    private val detail: String,
)

@OptIn(ExperimentalSerializationApi::class)
private fun MissingFieldException.toResponse(): ErrorResponse =
    ErrorResponse(
        type = this::class.java.name,
        title = "Missing Field Error",
        errors = missingFields
            .map {
                ErrorDetail(
                    detail = "Missing required field: $it",
                )
            },
    )

private fun SerializationException.toResponse(): ErrorResponse =
    ErrorResponse(
        type = this::class.java.name,
        title = "Serialization Error",
        errors = listOf(
            ErrorDetail(
                detail = message ?: "Unknown serialization error",
            ),
        ),
    )

private fun Throwable.toResponse(): ErrorResponse =
    ErrorResponse(
        type = this::class.java.name,
        title = "An unexpected error occurred",
        errors = listOf(
            ErrorDetail(
                detail = message.toString(),
            ),
        ),
    )
