# KIT721 Week 5 Android Tutorial

> Dongyi Guo, 662970

**why we use these constants instead of just typing in the string each time?**

- The value must be known at compile time (not calculated at runtime).

- The value cannot change during program execution (truly immutable).

**What function call do we use to tell the RecyclerView that we should update the list? What object is it called on?**

- `notifyDataSetChanged()` is a method in the `RecyclerView.Adapter` class.
- `notifyItemRangeInserted()` is a method in the `onCreate()` function.

**What was the purpose of tracking the movie.id in this example?**

- The purpose of tracking the movie.id is to ensure that the correct movie is updated based on user's touch.

**What does it mean when we say that the Firestore code is asynchronous? What are the implications of this for both writing the code and for usability? Which of Don Norman’s design principles are most relevant here?**

- Asynchronous code means that the code will not wait for the result of the function call before moving on to the next line of code. 
- This can make the code more efficient, but it can also make it more difficult to understand and debug.
- The most relevant design principle here is visibility, as the user needs to be able to see the results of their actions in order to understand what is happening.

**For those who have used relational databases like MySQL before, what are the advantages and disadvantages of using a NoSQL approach like Firestore?**

- Advantages:
  - NoSQL databases are more flexible than relational databases, as they do not require a fixed schema.
  - NoSQL databases can scale horizontally more easily than relational databases.
  - NoSQL databases can handle unstructured data more easily than relational databases.
- Disadvantages:
  - NoSQL databases are less mature than relational databases, so there are fewer tools and resources available.
  - NoSQL databases are less well-suited to complex queries than relational databases.
  - NoSQL databases are less well-suited to transactions than relational databases.