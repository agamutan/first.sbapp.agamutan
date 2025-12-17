package main.kotlin.com.agamutan.demo.test

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/test")
class TestController {
    @PostMapping
    fun create(@RequestBody body: Map<String, Any>): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.status(201).body(mapOf("ok" to true, "received" to body))
    }
}