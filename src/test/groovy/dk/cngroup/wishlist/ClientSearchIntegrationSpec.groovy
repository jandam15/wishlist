package dk.cngroup.wishlist

import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.ClientRepository
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.Wishlist
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.hasSize
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ClientSearchIntegrationSpec extends Specification {

    static final CONTROLLER_PATH = '/clients/search/findByUserName'
    static final CLIENT_CONTROLLER_SEARCHBYCODE_PATH = '/clients/search/findByProduct'

    @Autowired
    ClientRepository clientRepository
    @Autowired
    MockMvc mockMvc

    @Transactional
    def 'Expected JSON response is created for a valid request'() {
        given:
        def wishes = new Wishlist(products: [new Product(code: 'Sith Infiltrator')])
        def maul = new Client(active: true, firstName: 'Darth', lastName: 'Maul', wishes: [wishes])
        clientRepository.saveAndFlush(maul)

        when:
        def response = mockMvc.perform(get(CONTROLLER_PATH).param('userName', 'DARTH_MAUL'))

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.lastName', equalTo('Maul')))
    }

    def '404 is returned for invalid userName'() {
        when:
        def response = mockMvc.perform(get(CONTROLLER_PATH).param('userName', 'FOO'))

        then:
        response.andExpect(status().isNotFound())
    }

    @Transactional
    def 'List of clients that have desired products on their wishlists is returned'() {
        given:
        def wishes = new Wishlist(products: [new Product(code: 'Scimitar')])
        def wishes2 = new Wishlist(products: [new Product(code: 'scimitar')])

        def maul = new Client(active: true, firstName: 'Darth', lastName: 'Maul', wishes: [wishes])
        def vader = new Client(active: true, firstName: 'Darth', lastName: 'Vader', wishes: [wishes2])

        clientRepository.saveAllAndFlush([maul, vader])

        when:
        def response = mockMvc.perform(get(CLIENT_CONTROLLER_SEARCHBYCODE_PATH).param('code', 'scim'))

        then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$[0].lastName', equalTo("Maul")))
                .andExpect(jsonPath('$[1].lastName', equalTo("Vader")))
                .andExpect(jsonPath('$', hasSize(2)))

    }

    def 'When searched for product that is not on any wishlist return 404 not found'() {
        when:
        def response = mockMvc.perform(get(CLIENT_CONTROLLER_SEARCHBYCODE_PATH).param('code', 'FOO'))

        then:
        response.andExpect(status().isNotFound())
    }
}