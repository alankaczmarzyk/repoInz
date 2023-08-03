package inzynierka.backend.Config;

import java.util.List;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {

    /**
           To use Swagger type in browser:   http://localhost:8081/swagger-ui/index.html#/

     **/
    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8081");
        devServer.setDescription("Unifone server");


        Info info = new Info()
                .title("Unifone Swagger")
                .version("1.0")
                .description("Zapraszam bardzo serdecznie do testowania");

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}