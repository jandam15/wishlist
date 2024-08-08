package dk.cngroup.wishlist

import dk.cngroup.wishlist.entity.Client
import dk.cngroup.wishlist.entity.ClientRepository
import dk.cngroup.wishlist.entity.Product
import dk.cngroup.wishlist.entity.Wishlist
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("local")
@Component
class DatabaseInitializer(private val clientRepository: ClientRepository) : CommandLineRunner {
    override fun run(vararg args: String) {
        val tieFighter = Product(code = "TIE Fighter", description = "Standard Imperial starfighter")
        val deathStar = Product(code = "Death Star", description = "Armed space station")
        val starDestroyer = Product(code = "Star Destroyer", description = "Imperial capital ship")
        val imperialShuttle = Product(code = "Imperial Shuttle", description = "Transport ship")
        val sithInterceptor = Product(code = "Sith Interceptor")//, description = "Sith starfighter")
        val sithInterceptor2 = Product(code = "sith Interceptor")//, description = "Sith starfighter")
        val starCreator = Product(code = "Star Creator", description = "Made up ship")
        val wishlist = Wishlist(products = arrayListOf(tieFighter, deathStar, starDestroyer, starCreator, imperialShuttle, sithInterceptor, sithInterceptor2))
        val vader = Client(firstName = "Darth", lastName = "Vader")
        val wishlist2 = Wishlist(products = arrayListOf(tieFighter, imperialShuttle, starCreator))
        val obiwan = Client(firstName = "Obi-Wan", lastName = "Kenobi")

        vader.addWishlist(wishlist)
        obiwan.addWishlist(wishlist2)
        clientRepository.saveAll(arrayListOf(vader, obiwan))

    }
}