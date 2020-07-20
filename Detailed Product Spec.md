# Detailed Product Spec

# LivingTogether
#### Description
The purpose of this app is to act as a communal living messaging board. It has the benefits of a tool like Splitwise, but expands beyond monetary spending to include things like a communal chore list, shower scheduler, reminders about cleaning something, and a live shopping list. 

#### App Evaluation
- **Category:** Personal Organizer App
- **Mobile:** The mobile feature is important as it is all about live daily communication with your roommates/apartment mates. 
- **Story:** It's meant to streamline the process of constantly texting to see if anyone needs anything when you are heading to the grocery store, calculating how money should be split for appliances and food, eliminate confusion over shower times, and eliminate the need to create chore spreadsheets. 
- **Market:** Any young person in a communal living arrangement
- **Habit:** People frequently update when they go shopping etc. Would send alerts when other people are heading to the store, or sending reminders, shower schduele is changed (alerts can be turned on and off)
- **Scope:** A minimal version would basically be a group posting wall, similar to a facebook group, that can be edited by the group, but it could be scaled up to make it more all inclusive of all you might need in a communal living situation.


#### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* A group messaging and posting board. This could even be like the Twitter timeline, exept people can just post reminders or if they are going out to get groceries later that day. 
    * you would have to be able to respond to the messages, 
        * Your response type depends on the message type
    * stretch feature would be able to save someone else's message as a reminder--like long tap and it saves the event on your home screen
    * must be able to post photos-For example, I took a photo of mold on our shower curtain to show people how gross it was and how we need to better cleaning, and I could see something like this being on the main message board.
   
* Live shopping list (view only)



* Profile Page
    * can update picture/name 
    * can view you chores, your balance, your pinned post

* Generate a chore list each week (ie you give it a list of chores and it distrubutes them in cycle), and keep a live update on who has completed their chores
    * Profile is where you can view and check off the chores you have done
    * can view other's profiles by swipping left and right
    * stretch story: give people kudos for good job on chore
    * can swipe left or right to see other suitemates and give them kudos or to see if they have done their chores yet
    * logout button is here
    

##### Multiple compose templates
* General annoucement
    * can be taken from a facebook post
    * picture option
    * title option
    * text/body option
* Add Shopping item: List an item to buy
    * Text edit to put in the think you need
    * text edit to put in the quanitity
    * multiple people can like it/"ditto it" to show they also want the item
* Register a purchase  in the costs
    * allows you to choose the item you are puchasing off the shopping list (deletes it from List)
    * enter in the cost and sumbit photo of reciept 
    * automatically adds people who ditto'ed it to the bill so that people buying each thing are only paying for the things they liked/ditto'ed (then someone who doesn't drink milk, for example, wouldn't have to pay for milk)
    * presents a preview
    * when you submit it splits the bill, so that the items and balance appear on people profile page
* Scheuduler
    * text edit for event title
    * event description
    * can enter in date options
    * once it's posted people can check off dates to vote for which one 
    * comments also allowed

* Lost and Found
    * haven't figured this template out yet because I just created...

* Create a todo item
    * can list a new group chore
    * can assign it to a person
    * or can post it publically
        * Response type: someone can claim it and it will be added to their chore list on their profile
        * Or it will randomly assign it to someone
        

#### Requirements
1. Your app has multiple views
    - 5 tabs at the bottom
    - Home page that shows your saved messages
    - messaging board (with modal overlay for composing), 
        - message detail and replies
    - chore list/chore completion
    - scheduler page
        - event details page to make or edit events
    - grocery list
        - compose grocery item view
        - split money view where you can review the split and upload reciepts
    - login page
    - Maybe stretch: profile and you can check their chores and the status of them, can edit which groups you're in (ie if you have a summer living situation vs regular during the school year) 
- Your app interacts with a database (e.g. Parse)
    - Messaging board, users, housing groups, grocery list? chore lists?
- You can log in/log out of your app as a user
    - yes
- You can sign up with a new user profile
    - yes 
- Somewhere in your app you can use the camera to take a picture and do something with the picture (e.g. take a photo and share it to a feed, or take a photo and set a user’s profile picture)
    - reciepts, profile photo, post to messaging board
- Your app integrates with a SDK (e.g. Google Maps SDK, Facebook SDK)
    - integrates with facebook SDK to allow people to login though their facebook and retrieves facebook posts and formats them into annoucements posts in the app so people can post facebook posts in the app
- Your app contains at least one more complex algorithm (talk over this with your manager)
    - figuring out how to get each type of post to interact with the profile, the group setting etc.
- Your app uses gesture recognizers (e.g. double tap to like, e.g. pinch to scale)
    - maybe a pinch to finish an activity-when you're composing and you actually don't want to (it's like you're crumbling up a note), so you can just pinch to go back, double tap to like, long tap to delete items
- Your app use an animation (doesn’t have to be fancy) (e.g. fade in/out, e.g. animating a view growing and shrinking)
    - animated compose view expands to appear.
    - profile swipes up to appear
- Your app incorporates an external library to add visual polish
    - not sure about external libraries I can use yet. Would also like advice about this. Is Material design components a library?


First Three days: Goal get a main messaging board up and running so I can test my later messages (not sure how much of this I can get done in three days because I'm not sure of any issues I might come up with)...This basic part has been taking a week, so I hope I can do it in three or four days


Day 1:
    set up parse, facebook API, users, housing groups, login page and function, home messaging board xml, profile xml, message interface, set up message model for submit a reciept (maybe?), set up message model for shopping item (bc submit a reciept usses that)
    
Day 2: 
    set up Recyler view and adaptor (if the messages are in different formats do I use one adaptor? How do I bind each message type separately? should this be a switch statment--but then if photos are in different places do I just create a item xml template that has the features for everything and then just Visible.GONE things I'm not using for a certain template type?)
    if I finish that --set up compose for submit a reciept so that I can at least post that one type of message
    
Day 3: Finish the functionality of the first two message types ie xmls, text edits, submit a photo