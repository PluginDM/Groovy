package dmscratch2

import kotlinx.coroutines.*

fun main()
{

    val globalScope = CoroutineScope(Dispatchers.Default);

    runBlocking {



//-Dkotlinx.coroutines.debug    // Enables logging of coroutine name


        println("main STARTS")

        //!!! WAIT FOR BOTH asyncs TO COMPLETE !!!
        joinAll( /* vararg of Jobs*/
            async { threadSwitchingCoroutine(1, 500) },
            async { threadSwitchingCoroutine(2, 300) }
        )

        println("main runBlocking: ON ENTRY!! -  I'm working in thread ${Thread.currentThread().name}")

        this.launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
            println("Unconfined      : I'm working in thread ${Thread.currentThread().name}")
            delay(500)
            println("Unconfined      : After delay in thread ${Thread.currentThread().name}")


        }


        ///////////////////////////////////////////////////////////////////////////////////
        //WAITS FOR ALL JOBS TO COMPLETE BEFORE CONTINUING ....effectively performs a joinAll() !!!!!
        println("-----------------------------------------------------")
        coroutineScope {

            println("main runBlocking:coroutineScope!! I ${this}")
            println("main runBlocking:coroutineScope!! I'm working in thread ${Thread.currentThread().name}")

            this.async { threadSwitchingCoroutine(1, 500) }
            this.async { threadSwitchingCoroutine(2, 300) }
        }

        println("main runBlocking:coroutineScope!!  END OF BLOCK")

        println("-----------------------------------------------------")
        ///////////////////////////////////////////////////////////////////////////////////


        ///////////////////////////////////////////////////////////////////////////////////
        //!!!! DOES NOT WAIT FOR CHILD JOBS TO COMPLETE !!!!
        globalScope.launch {

            println("main runBlocking:globalScope ### I ${this}")
            println("main runBlocking:globalScope ### I'm working in thread ${Thread.currentThread().name}")

            async { threadSwitchingCoroutine(1, 500) }
            async { threadSwitchingCoroutine(2, 300) }
        }
        println("main runBlocking:globalScope ###  END OF BLOCK")
        ///////////////////////////////////////////////////////////////////////////////////


        this.launch(Dispatchers.Default) { // Default
            println("Default!!      : I'm working in thread ${Thread.currentThread().name}")
            delay(500)
            println("Default !!     : After delay in thread ${Thread.currentThread().name}")


        }


        this.launch { // context of the parent, main runBlocking coroutine
            println("main runBlocking: I'm working in thread ${Thread.currentThread().name}")
            delay(1000)
            println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
        }



    }


    println("main ENDS")

    /************* OUTPUT *******************
     main STARTS
    Coroutine 1 starts work on main @coroutine#2
    Coroutine 2 starts work on main @coroutine#3
    Coroutine 2 has finished on DefaultDispatcher-worker-1 @coroutine#3
    Coroutine 1 has finished on DefaultDispatcher-worker-1 @coroutine#2
    main runBlocking: ON ENTRY!! -  I'm working in thread main @coroutine#1
    Unconfined      : I'm working in thread main @coroutine#4
    -----------------------------------------------------
    main runBlocking:coroutineScope!! I "coroutine#1":ScopeCoroutine{Active}@4abdb505
    main runBlocking:coroutineScope!! I'm working in thread main @coroutine#1
    Coroutine 1 starts work on main @coroutine#5
    Coroutine 2 starts work on main @coroutine#6
    Coroutine 2 has finished on DefaultDispatcher-worker-1 @coroutine#6
    Unconfined      : After delay in thread kotlinx.coroutines.DefaultExecutor @coroutine#4
    Coroutine 1 has finished on DefaultDispatcher-worker-1 @coroutine#5
    main runBlocking:coroutineScope!!  END OF BLOCK
    -----------------------------------------------------
    main runBlocking:globalScope ###  END OF BLOCK
    main runBlocking:globalScope ### I "coroutine#7":StandaloneCoroutine{Active}@1bd07283
    main runBlocking:globalScope ### I'm working in thread DefaultDispatcher-worker-1 @coroutine#7
    Coroutine 2 starts work on DefaultDispatcher-worker-2 @coroutine#10
    main runBlocking: I'm working in thread main @coroutine#11
    Default!!      : I'm working in thread DefaultDispatcher-worker-3 @coroutine#9
    Coroutine 1 starts work on DefaultDispatcher-worker-2 @coroutine#8
    Coroutine 2 has finished on DefaultDispatcher-worker-2 @coroutine#10
    Default !!     : After delay in thread DefaultDispatcher-worker-3 @coroutine#9
    Coroutine 1 has finished on DefaultDispatcher-worker-2 @coroutine#8
    main runBlocking: After delay in thread main @coroutine#11
    main ENDS

     *************/


    /****


    SUMMARY:
    =======
    Let’s repeat the five mental model building blocks that we worked out:

    -------
    🧱 With coroutines, it is possible to define blocks of code are executed only
    partially before returning the control flow back to the call site. This works because
    coroutines can be suspended at suspension points and then resumed at a later point in time.
    -------
    🧱 Coroutines allow you to achieve concurrent behavior without switching threads, which
    results in more efficient code. Therefore, coroutines are often called “lightweight threads”.
    -------
    🧱 Coroutines can be seen as ABSTRACTIONS on top of threads. Different code blocks within the
    same coroutine can be executed in different threads.
    -------
    🧱 The compiler transforms suspend functions into regular functions that receive an additional
    continuation . Depending on the state of the continuation, different code of the suspend function
    is executed. That’s how a coroutine can be split up into separately running code blocks.
    -------
    🧱 delay() is not blocking its host thread because it, depending on the dispatcher, uses mechanisms
    like handler.postDelayed() . So it basically just schedules all subsequent operations of the
    coroutine to a later point in time.
    -------

    FINALLY:
    =======
    These building blocks have helped me a lot to finally wrap my head around coroutines and
    write better concurrent code.


     ****/

}

suspend fun threadSwitchingCoroutine(number: Int, delay: Long) {
    println("Coroutine $number starts work on ${Thread.currentThread().name}")
    delay(delay)
    withContext(Dispatchers.IO) {
        println("Coroutine $number has finished on ${Thread.currentThread().name}")
    }
}
