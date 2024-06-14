package dk.cngroup.wishlist.controller

import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.ClientRepository
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.ProductRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.rest.webmvc.PersistentEntityResource
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler
import org.springframework.data.rest.webmvc.RepositoryRestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.math.min

// enhancement of /clients endpoint generated by Spring Data REST
@RepositoryRestController
class ClientController(private val repository: ClientRepository) {

    // custom method returning loaded entity in Spring HATEOAS way
    @GetMapping("/clients/search/findByUserName")
    fun getByName(
        @RequestParam userName: String,
        resourceAssembler: PersistentEntityResourceAssembler
    ): ResponseEntity<PersistentEntityResource> {
        val client = repository.findClientByUserName(userName)
        return ResponseEntity.ok(resourceAssembler.toFullResource(client))
    }
}

@RestController
class ClientSearchController(
    private val productRepository: ProductRepository,
    private val clientRepository: ClientRepository
) {
    @GetMapping("clients/search/findByProduct")
    fun getByProduct(
        @RequestParam code: String,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "5") size: Int
    ): ResponseEntity<out Any?> {
        val clients = getClientsByProduct(
            clientRepository.findByIdIsNotNull(),
            productRepository.findByCodeStartingWithIgnoreCase(code, null)
        )
        val result = listToPage(clients, page - 1, size)


        if (result.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No client has product with desired code on wishlist")
        }
        return ResponseEntity.ok(result)
    }

    fun getClientsByProduct(clients: List<Client>, products: List<Product>): List<Client> {
        val result = mutableListOf<Client>()
        var productsOnWishlist = mutableListOf<Product>()

        for (client in clients) {
            productsOnWishlist = client.wishes[0].products
            for (i in 0 until productsOnWishlist.size) {
                for (j in 0 until products.size) {
                    if (productsOnWishlist[i].code == products[j].code) {
                        if(client.productCode == null)
                            client.productCode = "${products[j].code};"
                        else
                            client.productCode += "${products[j].code};"

                        result += client
                    }
                }
            }
        }
        return result
    }

    private fun listToPage(list: List<Client>, page: Int, size: Int): PageImpl<Client?> {
        val pageRequest = createPageRequest(page, size)
        val start = pageRequest?.offset?.toInt()
        val end = min((start?.plus(pageRequest.pageSize.toInt())!!), list.size)
        val pageContent = list.subList(start, end)
        val resultPage = PageImpl<Client?>(pageContent, pageRequest, list.size.toLong())
        return resultPage
    }

    private fun createPageRequest(page: Int, size: Int): PageRequest? = PageRequest.of(page, size)
}