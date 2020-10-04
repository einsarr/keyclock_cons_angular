package ecom.ecomapp;

import groovy.transform.Undefined;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.compat.JrePlatform;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.facade.SimpleHttpFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Supplier;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
class Product{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
}
@Repository
interface ProductRepository extends JpaRepository<Product,Long> {

}
@Controller
class ProductController{
    @Autowired
    private ProductRepository productRepository;
    @RequestMapping("/")
    public String index(){
        return "index";
    }
    @RequestMapping("/products")
    public String products(Model model){
        model.addAttribute("productss",productRepository.findAll());
        return "products";
    }
}

@SpringBootApplication
public class EcomAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcomAppApplication.class, args);
    }

    @Bean
    CommandLineRunner start(ProductRepository productRepository){
        return args -> {
            productRepository.save(new Product(null,"ORD",80000));
            productRepository.save(new Product(null,"Imprimanete",100));
            productRepository.save(new Product(null,"Smart phone",9900));
            productRepository.findAll().forEach(p->{
                System.out.println(p.getName());
            });
        };
    }
}
@Configuration
class KeycloakConfig{
    @Bean
    KeycloakSpringBootConfigResolver configResolver(){
        return new KeycloakSpringBootConfigResolver();
    }
    @Bean
    KeycloakRestTemplate keycloakRestTemplate(KeycloakClientRequestFactory keycloakClientRequestFactory){
        return new KeycloakRestTemplate(keycloakClientRequestFactory);
    }
}
@KeycloakConfiguration
class KeycloakSpringSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(keycloakAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.authorizeRequests().antMatchers("/products/**").authenticated();
    }
}
@Controller
class SecurityController{
    @Autowired
    private AdapterDeploymentContext adapterDeploymentContext;
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request){
        try {
            request.logout();
        } catch (ServletException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }
    @GetMapping("/changePassword")
    public String changePassword(RedirectAttributes attributes,
                                 HttpServletRequest request,
                                 HttpServletResponse httpServletResponse){
        HttpFacade facade = new SimpleHttpFacade(request, httpServletResponse);
        KeycloakDeployment deployment = adapterDeploymentContext.resolveDeployment(facade);
        attributes.addAttribute("referrer",deployment.getResourceName());
        return "redirect:"+deployment.getAccountUrl()+"/password";
    }
}

@Controller
class SuppliersController{
    @Autowired
    private KeycloakRestTemplate keycloakRestTemplate;

    @GetMapping("/suppliers")
    public String suppliers(Model model){
        ResponseEntity<PagedModel<Supplier2>> response = keycloakRestTemplate.exchange("http://localhost:8083/suppliers", HttpMethod.GET, null, new ParameterizedTypeReference<PagedModel<Supplier2>>() {
        });
        model.addAttribute("supplierss",response.getBody().getContent());
        return "suppliers";
    }
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(){
        return "errors";
    }

}
@Data
class Supplier2{
    private Long id;
    private String name;
    private String email;
}
