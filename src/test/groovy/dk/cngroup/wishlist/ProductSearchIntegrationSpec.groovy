package dk.cngroup.wishlist

import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.hamcrest.Matchers.contains
import static org.hamcrest.Matchers.containsString
import static org.hamcrest.Matchers.equalTo
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

    def 'get list of products by description'() {
        given:
        def product = new Product(code: 'New fighter', description: 'Unknown fighter')
        def product2 = new Product(code: 'New fighter2', description: 'Unknown')
        productRepository.saveAndFlush(product)
        productRepository.saveAndFlush(product2)

        when:
        def response = mockMvc.perform(get(PRODUCT_CONTROLLER_PATH).param('description', 'Unknown'))

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$[0].description', containsString('Unknown')))
    }

    def 'return 404 not found for invalid description'() {
        when:
        def response = mockMvc.perform(get(PRODUCT_CONTROLLER_PATH).param('description', 'FOO'))

        then:
        response.andExpect(status().isNotFound())
    }
}
