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

    def 'when products are searched by description, all entries with description containing keyword are returned'() {
        given:
        def product  = new Product(code: 'New fighter', description: 'Unknown fighter')
        def product2 = new Product(code: 'New fighter2', description: 'Unknown')

        productRepository.saveAllAndFlush([product, product2])

        when:
        def response = mockMvc.perform(get(PRODUCT_CONTROLLER_PATH).param('description', 'Unknown'))

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$[0].description', containsString('Unknown')))
                .andExpect(jsonPath('$', hasSize(2)))
    }

    def 'when products are searched by description not present in database, 404 not found is returned'() {
        when:
        def response = mockMvc.perform(get(PRODUCT_CONTROLLER_PATH).param('description', 'FOO'))

        then:
        response.andExpect(status().isNotFound())
    }
}
