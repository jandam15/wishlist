package dk.cngroup.wishlist

import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.hamcrest.Matchers.containsString
import static org.hamcrest.Matchers.hasSize
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProductSearchIntegrationSpec extends Specification {

    static final PRODUCT_CONTROLLER_PATH = '/products/search/findByDescription'

    @Autowired
    ProductRepository productRepository
    @Autowired
    MockMvc mockMvc

    def 'when search with valid description is invoked, product list of correct size is returned'() {
        given:
        def products = [
                new Product(code: 'New fighter', description: 'Unknown fighter'),
                new Product(code: 'New fighter2', description: 'Unknown'),
        ]
        productRepository.saveAllAndFlush(products)

        when:
        def response = mockMvc.perform(get(PRODUCT_CONTROLLER_PATH).param('description', 'Unknown'))

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$[0].description', containsString('Unknown')))
                .andExpect(jsonPath('$', hasSize(2)))
    }

    def 'when search with invalid description is invoked, 404 not found is returned'() {
        when:
        def response = mockMvc.perform(get(PRODUCT_CONTROLLER_PATH).param('description', 'FOO'))

        then:
        response.andExpect(status().isNotFound())
    }
}
