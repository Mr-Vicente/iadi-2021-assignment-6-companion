package pt.unl.fct.di.iadidemo.bookshelf.presentation.api

import org.springframework.web.bind.annotation.*

interface GenAPI<S,T,U> { // S - InDTO, T - ListDTO, U - LongDTO
    @GetMapping
    fun getAll(@RequestParam(required = false, defaultValue = "0") page: Int, @RequestParam(required = false, defaultValue = "3") size: Int):List<T>

    @PostMapping
    fun addOne(@RequestBody elem:S): U

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): U

    @PutMapping("{id}")
    fun updateOne(@PathVariable id: Long, @RequestBody elem:S) : U

    @DeleteMapping("{id}")
    fun deleteOne(@PathVariable id:Long):Unit;
}

