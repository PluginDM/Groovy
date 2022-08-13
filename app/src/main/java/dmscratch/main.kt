package dmscratch

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.EmptyCoroutineContext


fun main()
{
    //simpleListOnMainThread().forEach { value -> println("simpleListOnMainThread():$value")}

    runBlocking{

        simpleListNonBlocking().forEach { value -> println("simpleListNonBlocking(): $value" )}
    }


    //simpleSequenceOnMainThread().forEach { value -> println("simpleSequenceOnMainThread(): $value")}


    // Launch a concurrent coroutine to check if the main thread is blocked


    runBlocking <Unit>{


        println("PARENT-" + this)
       /*this.*/launch {

        println("child-" + this)
        for (i in 1..3) {
                println("I'm not blocked")

                delay(100)
            }
        }

        //NOTE: suspend keyword NOT REQUIRED for method simpleFlow() WHICH BUILDS A Flow- but COULD BE ADDED !!!!
        simpleFlow().collect{ value -> println("simpleFlow(): $value") }
    }

    /**  OUTPUT
     *
    simpleListNonBlocking(): 1
    simpleListNonBlocking(): 2
    simpleListNonBlocking(): 3
    PARENT-BlockingCoroutine{Active}@4f063c0a
    simpleFlow(): 1
    child-StandaloneCoroutine{Active}@9a7504c
    I'm not blocked
    simpleFlow(): 2
    I'm not blocked
    simpleFlow(): 3
    I'm not blocked
    dummyDownloadUserDataInCallerParentScope(): 100
    dummyDownloadUserDataInNonParentScope() TOP: BlockingCoroutine{Active}@5bfbf16f
    dummyDownloadUserDataInNonParentScope() BOTTOM: ScopeCoroutine{Active}@12cdcf4
    dummyDownloadUserDataInNonParentScope(): 0



     */


    runBlocking {

        val retVal = dummyDownloadUserDataInCallerParentScope()

        println("dummyDownloadUserDataInCallerParentScope(): $retVal" )
    }


    runBlocking {
        println("dummyDownloadUserDataInNonParentScope() TOP: $this" )

        coroutineScope {
            println("dummyDownloadUserDataInNonParentScope() BOTTOM: $this" )
        }
        val retVal = dummyDownloadUserDataInNonParentScope()

        println("dummyDownloadUserDataInNonParentScope(): $retVal" )
    }




}

fun simpleListOnMainThread(): List<Int> = listOf(1,2,3)

fun simpleSequenceOnMainThread(): Sequence<Int> = sequence{
    for(i in 1..3)
    {
        //This blocks the main thread.

        Thread.sleep(100) //Pretend we are computing something that takes time.

        //...where "this" is the SequenceScope
        this.yield(i)  //Yield next value
    }
}

suspend fun simpleListNonBlocking(): List<Int> {

    delay(100) // pretend we are doing something asynchronous here

    return listOf(1, 2, 3)
}

//NOTE: suspend keyword NOT REQUIRED - but COULD BE ADDED !!!!
//HAVE TO DEFINE RETURN TYPE OF FLOW - OTHERWISE COMPILER COMPLAINS !!!!
/*suspend*/ fun simpleFlow(): Flow<Int> = flow{

    for(i in 1..3)
    {
        emit(i)
        delay(100)
    }
}

//Unstructured Concurrency
private suspend fun dummyDownloadUserDataInNonParentScope(): Int {
    var result = 0
    // Here, we use CoroutineScope (Capital C version) which will start a new scope and
    // launch coroutine in new scope Dispatchers.IO, Not In Parent Scope which is Dispatchers.Main
    // Thus, this function would directly return without waiting for loop completion and will return 0

    /*
    This is an example of Unstructured Concurrency where it is not guaranteed that child coroutine would
    complete before returning. Thus, caller/parent coroutine would get wrong value returned by child coroutine.
    Even, when child coroutine has returned already, child coroutine may be running (in Active state) in the
     background which may lead to Memory Leaks in certain cases.

Solution :

When we need to communicate between multiple coroutines, we need to make sure Structured
Concurrency (Recommended)

This can be done by re-using parent/caller coroutine scope inside child/callee coroutine.
This can be achieved by coroutineScope {} (Smaller c) version inside child/callee coroutine.
     */

    CoroutineScope(Dispatchers.IO).launch {
        for (i in 0 until 100) {
            kotlinx.coroutines.delay(10)
            result++
        }
    }
    return result
}


//Unstructured Concurrency
private suspend fun dummyDownloadUserDataInCallerParentScope(): Int {
    var result = 0
    // By using coroutineScope (Smaller c version) below, we ensure that this coroutine would execute in the
    // parent/caller coroutine's scope, so it would make sure that the for loop would complete
    // before returning from this suspended function. This will return 20000 properly

    /*
      Re-using parent/caller coroutine scope.

    It's the suspending equivalent of runBlocking, because it waits
    for ALL child coroutines to finish their execution before returning:


     */
    coroutineScope {
        for (i in 0 until 100) {
            kotlinx.coroutines.delay(10)
            result++
        }
    }
    return result
}


fun simpleLaunch() = runBlocking{

    //... from kotlinx.coroutines.yield
    yield()
}

/*
Unlike launch or async, runBlocking is a very special coroutine builder because it is supposed to be used at the top-level.
 It is therefore not run inside a scope (there is no CoroutineScope as receiver as you can see). It is rather meant to be
 a "root" of structured concurrency.

runBlocking actually provides you with a scope, so you can start "child" coroutines inside it. What really matters is
that runBlocking will wait for all coroutines that you start in that scope (child coroutines) to finish before returning.

Cancelling runBlocking?
You cannot really cancel the runBlocking coroutine itself externally if it hangs (like you would do by cancelling
the scope of launch or async), because it's not an async task - it's blocking a thread. You could interrupt the
thread running it, or you could also keep track of nested coroutines and cancel them explicitly to make runBlocking
finish.

A runBlocking call will only return once all child coroutines have completed (or were cancelled).
 Throwing an exception (from inside) also cancels all child coroutines and makes runBlocking rethrow
  that exception. So it's also a way to "cancel" runBlocking from inside.

Is it local to the class where it is called and the Coroutine is garbage collected when the Class is
 garbage collected or will it cause memory leak if its still running?

You could see runBlocking as any blocking function, like Thread.sleep, there is no more magic. Just
like Thread.sleep, runBlocking can be called from any function, even top-level, in which case there
 wouldn't be any class instance involved.

Let's assume a method in a class calls runBlocking, and whatever is inside runBlocking hangs for a
long time (like a long sleep). Then, whoever is calling this method holds a reference to the instance until the method returns or fails, so the instance won't be garbage collected anyway. In this case the caller will hang, blocking whatever thread it's running in - that's where leaks can happen.
 */


/*
QUESTION
========
Is there any difference between this two approaches?

runBlocking {
   launch(coroutineDispatcher) {
      // job
   }
}
GlobalScope.launch(coroutineDispatcher) {
   // job
}
kotlin
kotlin-coroutines
coroutinescope

ANSWER
======

runBlocking runs new coroutine and blocks current thread interruptibly until its completion.
This function should not be used from coroutine. It is designed to bridge regular blocking
code to libraries that are written in suspending style, to be used in main functions and in tests.

// line 1
runBlocking {
   // line 2
   launch(coroutineDispatcher) {
      // line 3
   }
   // line 4
}
// line 5
someFunction()
In case of using runBlocking lines of code will be executed in the next order:

line 1
line 2
line 4
line 3
line 5 // this line will be executed after coroutine is finished
Global scope is used to launch top-level coroutines which are operating on the whole application
lifetime and are not cancelled prematurely. Another use of the global scope is operators running in Dispatchers.Unconfined, which don't have any job associated with them. Application code usually should use application-defined CoroutineScope, using async or launch on the instance of GlobalScope is highly discouraged.

// line 1
GlobalScope.launch(coroutineDispatcher) {
   // line 2
}
// line 3
someFunction()
In case of using GlobalScope.launch lines of code will be executed in the next order:

line 1
line 3
line 2
Thus runBlocking blocks current thread until its completion, GlobalScope.launch doesn't.

 */

/****

QUESTION: coroutineScope IDENTICAL to withContext(this.coroutineContext)   ?????
================================================================================

Nice answer, but it is NOT TRUE that coroutineScope is basically an alias for withContext(this.coroutineContext):
they are not the same thing. withContext will NOT wait for sub-coroutines you launch inside it to COMPLETE,
coroutineScope WILL –
Daniele Segato
Sep 2, 2020 at 9:56



CoroutineScope() is nothing but a FACTORY of CoroutineScope objects, and a CoroutineScope object is nothing but a
HOLDER of a CoroutineContext. It has no ACTIVE role in coroutines, but it's an important part of the infrastructure
that makes it easy to do structured concurrency properly. This comes from the fact that all coroutine builders
like launch or async are EXTENSION functions on CoroutineScope and INHERIT its context.

You will rarely, if ever, have the need to call CoroutineScope() because usually you either pick up an existing
coroutine scope or have one created for you by other convenience functions (like MainScope on Android)
or Kotlin internals.


public interface CoroutineScope
{

 //The context of this scope.
 // Context is encapsulated by the scope and used for implementation of coroutine builders that
are extensions on the scope.
 //Accessing this property in general code is not recommended for any purposes except accessing
the [Job] instance for advanced usages.
 //
 //By convention, should contain an instance of a [job][Job] to enforce structured concurrency.

public val coroutineContext: CoroutineContext
}


public object GlobalScope : CoroutineScope {

 // Returns [EmptyCoroutineContext].

override val coroutineContext: CoroutineContext
get() = EmptyCoroutineContext
}
 ***/


/**
 * [CoroutineScope] defines a scope for new coroutines. Every **coroutine builder** (like [launch], [async], etc)
 * is an extension on [CoroutineScope] and inherits its [coroutineContext][CoroutineScope.coroutineContext]
 * to automatically propagate all its elements AND cancellation.
 *
 * The best ways to obtain a standalone instance of the scope are [CoroutineScope()] and [MainScope()] factory functions.
 * Additional context elements can be appended to the scope using the [plus][CoroutineScope.plus] operator.
 *
 * ### Convention for structured concurrency
 *
 * Manual implementation of this interface is not recommended, implementation by delegation should be preferred instead.
 * By convention, the [context of a scope][CoroutineScope.coroutineContext] should contain an instance of a
 * [job][Job] to enforce the discipline of **structured concurrency** with propagation of cancellation.
 *
 * Every coroutine builder (like [launch], [async], etc)
 * and every scoping function (like [coroutineScope], [withContext], etc) provides _its own_ scope
 * with its own [Job] instance into the inner block of code it runs.
 * By convention, they all wait for all the coroutines inside their block to complete before completing themselves,
 * thus enforcing the structured concurrency. See [Job] documentation for more details.
 *
 * ### Android usage
 *
 * Android has first-party support for coroutine scope in all entities with the lifecycle.
 * See [the corresponding documentation](https://developer.android.com/topic/libraries/architecture/coroutines#lifecyclescope).
 *
 * ### Custom usage
 *
 * [CoroutineScope] should be implemented or declared as a property on entities with a well-defined lifecycle that are
 * responsible for launching children coroutines, for example:
 *
 * ```
 * class MyUIClass {
 *     val scope = MainScope() // the scope of MyUIClass
 *
 *     fun destroy() { // destroys an instance of MyUIClass
 *         scope.cancel() // cancels all coroutines launched in this scope
 *         // ... do the rest of cleanup here ...
 *     }
 *
 *     /*
 *      * Note: if this instance is destroyed or any of the launched coroutines
 *      * in this method throws an exception, then all nested coroutines are cancelled.
 *      */
 *     fun showSomeData() = scope.launch { // launched in the main thread
 *        // ... here we can use suspending functions or coroutine builders with other dispatchers
 *        draw(data) // draw in the main thread
 *     }
 * }
 *
 */


/***

They are two completely different things.

!! CoroutineScope is the interface that define the concept of Coroutine Scope: to launch and
create coroutines you need a one.

!!GlobalScope is a instance of scope that is global for example.

!!CoroutineScope() is a global function that creates a CoroutineScope

When you have a scope you can do launch() or async() or any other method related to executing coroutines.

// create a context
val myContext = Dispacher.IO
// you can combine dispachers, parent jobs etc.
// create the new scope
val myScope: CoroutineScope = CoroutineScope(myContext)
// returns immediately (unless you specify a start mode that run immediately)
val job = myScope.launch {
// suspend calls are allowed here cause this is a coroutine
}
// this code is executed right away
you can do this from outside a coroutine (plain code).

coroutineScope() on the other hand is an global suspend function that creates a new CoroutineScope under the hood and then execute the suspend function you pass with it in the body, and wait for it (and all its children) to complete before returning. It is a suspend function so you cannot call it outside of a coroutine.

// must be inside a coroutine here!

// this create a new CoroutineScope,
// then launch the given coroutine,
// then wait for it to complete
val result = coroutineScope {
// your coroutine here, which run immediately
return@coroutineScope "my result"
}
// this code is executed after the coroutine above is completed
// I can use "result" here
similar to coroutineScope there's supervisedScope which has only 1 difference: multiple children coroutines
(launch / async / ...) executed inside it will not cancel other children if one fails cause it use a SupervisorJob



CoroutineScope() is the method which takes a Context as input and gives you Context with a Job as an object of
CoroutineScope interface.

You can use this object to launch a coroutine job as following:

suspend fun doNotDoThis() {
CoroutineScope(coroutineContext).launch {
println("I'm confused")
}
}
While, coroutineScope() takes a block/labmda to execute as a coroutine job:

fun main() = runBlocking { // this: CoroutineScope
launch {
delay(200L)
println("Task from runBlocking")
}

coroutineScope { // Creates a new coroutine scope
launch {
delay(500L)
println("Task from nested launch")
}

delay(100L)
println("Task from coroutine scope") // This line will be printed before nested launch
}

println("Coroutine scope is over") // This line is not printed until nested launch completes
}


https://elizarov.medium.com/coroutine-context-and-scope-c8b255d59055

https://proandroiddev.com/how-to-make-sense-of-kotlin-coroutines-b666c7151b93
 ***/





