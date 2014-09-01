package ratpack.examples.book

import ratpack.example.books.Book
import ratpack.example.books.BookRestEndpoint
import ratpack.example.books.BookService
import ratpack.rx.RxRatpack
import spock.lang.Specification

import static ratpack.groovy.test.GroovyUnitTest.handle

class BookRestEndpointUnitSpec extends Specification {

    def setup() {
        RxRatpack.initialize()
    }

    def "will render book"() {
        given:
        def book = new Book("1932394842", 10, 22.22, "Groovy in Action", "Dierk Koenig", "Manning Publications")

        rx.Observable<Book> findObservable = rx.Observable.just(book)

        def bookServices = Mock(BookService)
        bookServices.find("1932394842") >> findObservable

        when:
        def result = handle(new BookRestEndpoint(bookServices)) {
            uri "1932394842"
            method "get"
            header "Accept", "application/json"
        }

        then:
        with(result) {
            rendered(Book) == book
        }
    }

    def "will return 404 if book not found"() {
        given:
        rx.Observable<Book> findObservable = rx.Observable.just(null)

        def bookServices = Mock(BookService)
        bookServices.find("1932394842") >> findObservable

        when:
        def result = handle(new BookRestEndpoint(bookServices)) {
            uri "1932394842"
            method "get"
            header "Accept", "application/json"
        }

        then:
        with(result) {
            clientError == 404
        }
    }

    def "will delete book"() {
        given:
        rx.Observable<Book> deleteObservable = rx.Observable.just(null)

        def bookServices = Mock(BookService)
        1 * bookServices.delete("1932394842") >> deleteObservable

        when:
        def result = handle(new BookRestEndpoint(bookServices)) {
            uri "1932394842"
            method "delete"
            header "Accept", "application/json"
        }

        then:
        with(result) {
            bodyText == ""
            status.code == 200
        }
    }

}
