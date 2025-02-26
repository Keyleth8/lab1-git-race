package es.unizar.webeng.hello.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import es.unizar.webeng.hello.QuoteRepository
import es.unizar.webeng.hello.Quote
import kotlin.random.Random
import org.springframework.data.repository.findByIdOrNull

/**
 * The annotation `@Controller` serves as a specialization of `@Component` and it allows us to
 * implement a web controller that handles templates.
 * 
 * This controller handles the page related to the stored quotes.
 */
@Controller
class QuoteController(private val repository: QuoteRepository) {
    
    /**
     * This function acts as the handler of the QuoteController.
     * When the url is requested without parameters, it displays a random quote
     * among the available ones.
     * If there are parameters, it stores the quote and its author, and then
     * displays a quote and the author.
     * 
     * @param inputQuote quote to add to the database if diferent from ""
     * @param inputAuthor name of the person who said the quote if diferent from ""
     * @param model collection with the data used to update the view (template)
     * @return the template with the updated information
     */
    @GetMapping("/quotes")
    fun quotes(@RequestParam(defaultValue = "") inputQuote: String,
        @RequestParam(defaultValue = "") inputAuthor: String,
        model: MutableMap<String, Any>): String {
        if (inputQuote != "" && inputAuthor != "") {
            // Save only if both parameters are correct
            repository.save(Quote(inputQuote, inputAuthor))
        }

        // Default to display when the database is empty
        var displayedQuote = Quote("It seems there aren't any quotes yet.", "Unknown")

        // Obtain max and min id in the database to obtain a valid, random id
        // It will be supposed to exist while no one can delete quotes
        var minId = repository.findMinId()
        if (minId != null) {
            var maxId = repository.findMaxId()
            if (maxId != null) { // It should always be true
                var id = Random.nextLong(maxId+1 - minId) + minId
                var quote = repository.findByIdOrNull(id)
                if (quote != null) {
                    displayedQuote = quote
                }
            }
        }
        model["quote"] = displayedQuote.quote
        model["saidBy"] = displayedQuote.saidBy
        return "quotes"
    }
}