/**@ai
 * Implementing Gdx-AI
 *
NOTES==============================START GDX-AI INFO================================
Infrastructure of gdxAI:
1) Message Handling
   - telegrams sent b/w agents about states
2) Scheduling
   - deals with figuring out ahead of time which algorithms to implement to process animState changes
   - used in order to keep device from using too many resources at one time

@MessageTypes:
 - Data Structure: in gdxAI, known as Telegram
   - info about sender, recipient, time
   - Fields relevant to game:
       location of sender & recipient, distances from opponents, overall status
2 Types of Telegrams
1) Immediate Telegram: immediately sent to recipient
2) Delayed Telegrams: They store the time at which they should be delivered. That way the routing system that
   receives these message objects can retain them until it's time to be delivered, effectively creating timers. These
   message timers are best used when a game object sends a message to itself, at a later time, to create an internal
   event timer.

Dispatching Messages:
- dispatched by MessageDispatcher class
- if only one instance needed, messageDispatcher can be replaced with MessageManager.getInstance().
Method for sending messages:
   messageDispatcher.dispatchMessage(
        delay,        // Immediate message if <= 0; delayed otherwise (delay is expressed in seconds.)
        sender,       // It can be null
        recipient,    // It can be null, see the "Multiple Recipients" section below
        messageType,  // Any user-defined int code
        extraInfo);   // Optional data accompanying the message
^
|
- MessageDispatcher uses this information to create a Telegram, which it either dispatches immediately (if the given
delay is <= 0) or stores in a queue (when the given delay is > 0) ready to be dispatched at the correct time.
- Just one argument is indeed mandatory, the messageType

Dispatching Messages
- If you send a message without specifying the recipient the message will be dispatched to all the agents listening
to that message type. Agents can register and unregister their interest in specific message types.
   // Lets the agent listent to msgCode
    messageDispatcher.addListener(agent, msgCode);

    // Lets the agent listent to the given selection of msgCodes
    messageDispatcher.addListener(agent, msgCode1, msgCode2, ...);

    // Removes msgCode from the interests of the agent
    messageDispatcher.removeListener(agent, msgCode);

    // Removes the given msgCodes from the interests of the agent
    messageDispatcher.removeListener(agent, msgCode1, msgCode2, ...);

    // Removes all the agents listening to msgCode
    messageDispatcher.clearListeners(msgCode);

    // Removes all the agents listening to the given selection of msgCodes
    messageDispatcher.clearListeners(msgCode1, msgCode2, ...);

    // Removes all the agents listening to any message type
    messageDispatcher.clearListeners();

Updating Dispatcher
- queued telegrams examined during each update step, if front of message queue contains
expired message, sent to recipient & removed from the queue
    messageDispatcher.update(delta) //as in delta in Gdx.graphics.getDeltaTime()

Receiving Messages
- When a telegram is received by an agent (actually a Telegraph), its method handleMessage(telegram) is invoked.
This method returns a boolean value indicating whether the message has been handled successfully.
- should never keep a reference to the telegram since telegrams are pooled

Telegram Providers
- Telegraph cannot access some info without hard references to sources of those infos
   - infos carried by Telegram dispatched BEFORE registration
   - infos held by other agents
- Solution is TelegramProvider interface
A TelegramProvider allows the MessageDispatcher to provide newly registered Telegraph with immediate Telegram.
Providers can register and unregister their ability to provide informations for specific message types.

      ///Lets the provider respond when a new Telegraph starts listening to msgCode
    messageDispatcher.addProvider(provider, msgCode);

    // Lets the provider respond when a new Telegraph starts listening to msgCode1, msgCode2, ...
    messageDispatcher.addProviders(provider, msgCode1, msgCode2, ...);

    // Removes all the providers
    messageDispatcher.clearProviders();

    // Removes all the providers responding to new Telegraph listening to msgCode
    messageDispatcher.clearProviders(msgCode);

    // Removes all the providers responding to new Telegraph listening to msgCode1, msgCode2, ...
    messageDispatcher.clearProviders(msgCode1, msgCode2, ...);
- When a new Telegraph starts listening to a specific type of message, the TelegramProvider can decide to provide or
not, extra information that will be immediately delivered to the Telegraph by the MessageDispatcher.
    Object provideMessageInfo (int msg, Telegraph receiver);

Saving & Restoring Pending Messages:
- when game saved, need to serialize & deserialize pending message at time T
- to deserialize, simply add message back to MessageDispather as normal
    messageDispatcher.scanQueue(new PendingMessageCallback() {
        @Override
        public void report (float delay, Telegraph sender, Telegraph receiver, int message, Object extraInfo) {
        // Here you can serialize the pending message.
        // Notice that pending messages are reported in any particular order.
        }
    });
 
@Scheduler
3 Main Ingredients to make best use of limited processing time available:
1) Dividing up execution time among the AI that needs it
2) Having algorithms that can work a bit at a time over several frames
3) Giving preferential treatment to important characters (units) & areas (units near opponent)

A Scheduler works by assigning a pot of execution time among a variety of tasks, based on which ones need the time.
A task must implement the Schedulable interface in order to be scheduled.

Hierarchical Scheduling: The Scheduler interface extends the Schedulable interface, allowing a scheduling system to
be run as a task by another scheduler. This technique is known as hierarchical scheduling.

AI Level of Detail: level of detail (LOD) systems. AI LOD systems are behavior selectors; they choose only one
behavior to run. In a hierarchical structure this means that schedulers running the whole game don't need to know
which behavior each character is running.

Types of Schedulers:
1) Frequency: take tasks, each one having a frequency and a phase that determine when it should be run.
- On each time frame, the scheduler is called to manage the whole AI budget.
- keeps count of number of frames passed, incrementing each time scheduler is called
- to test if eneded, check if frame count is evenly divisible by some frequency
2) A LoadBalancingScheduler understands the time it has to run and distributes this time equally among the tasks
that need to be run. This scheduler splits the time it is given according to the number of tasks that must be run on
this frame. To adjust for small errors in the running time of tasks, this scheduler recalculates the time it has left
after each task is run. This way an overrunning task will reduce the time that is given to others run in the same
frame.
3)A PriorityScheduler works like a LoadBalancingScheduler but allows different tasks to get a different share of
the available time by assigning a priority to each task. This scheduler splits the time it is given proportionally to
the priority of the tasks that must be run on this frame. In other words, the higher the priority value the larger
the amount of the available time dedicated to the corresponding task.

StateMachines:
LibGDX-AI implements Finite State Machines (FSMs) following way:
through embedded rules, thus hard-coding the rules for the animState transitions within the states
themselves. This architecture is known as the animState design pattern..implementing animState-driven behavior
with minimal overhead.

State Interface:
states of the FSM are encapsulated as objects and contain the logic required to facilitate animState transitions
- enter(entity) will execute when the animState is entered
- update(entity) is called on the current animState of the FSM on each update step
- exit(entity) will execute when the animState is exited
- onMessage(entity, telegram) executes if the entity receives a message from the message dispatcher while it is in this animState

Flow: animState transition occurs --> currState.exit() animState --> currState = new animState --> currState.enter()
- NOTES on FSM design
Singleton for Concrete State: use a singleton - this allows for less memory to be used when calling upon states
Drawback is that singleton states are unable to make use of there own local, agent-specific data since shared.
Global State: Often, when designing finite animState machines you'll end up with code that is duplicated in every animState.
When it happens it's convenient to create a global animState that is called every time the FSM is updated. That way, all
the logic for the FSM is contained within the states and not in the agent class that owns the FSM.
State Blip: Occasionally it will be convenient for an agent to enter a animState with the condition that when the animState
is exited, the agent returns to its previous animState. This behavior is called a animState blip. For instance, in the Far
West example below the agent Elsa can visit the bathroom at any time, then she always returns to its prior animState.

See following links for more info
DefaultStateMachine: {@link "https://github.com/libgdx/gdx-ai/wiki/State-Machine#defaultstatemachine"}
StackStateMachine: {@link "https://github.com/libgdx/gdx-ai/wiki/State-Machine#stackstatemachine"}

BEHAVIOR TREES
- more complex
Behavior Trees {@link "https://github.com/libgdx/gdx-ai/wiki/Behavior-Trees"}
Behavior Trees increase [FSM] modularity by encapsulating logic transparently within the states, making states
nested within each other and thus forming a tree-like structure, and restricting transitions to only these nested
states.
 
####Leaf Tasks####

Terminal nodes of tree, defining low level actions that describe overall behavior.

2 Types of Leaf Tasks:
1) Actions: execution of methods or functions on game world (ie attack or move)
2) Conditions: query the animState of objects in the game world (ie location of unit)

Example of eaf tasks in root avoid combat:

+--------------+
| Avoid combat |
+--------------+
/         \
+----------------+     +----------+
| Enemy visible? |     | Run away |
+----------------+     +----------+

<b>Composite Tasks</b>

Composite tasks provide a standard way to describe relationships between child tasks, such as how and when they should
be executed. They allow you to build branches of the tree in order to organise their sub-tasks (the children).
Basically, branches keep track of a collection of child tasks (conditions, actions, or other composites), and their
behavior is based on the behavior of their children.

4 Types of Composite Tasks:

1) *Selector*
A selector is a branch task that runs each of its child behaviors in turn. It will return immediately with a success
status code when one of its children runs successfully. Represented graphically by a "?"

2) *Sequence*
A sequence is a branch task that runs each of its child behaviors in turn. It will return immediately with a failure
status code when one of its children fails. As long as its children are succeeding, it will keep going.
Represented by an arrow: "-->"

3) *Decorator*
The name "decorator" is taken from object-oriented software engineering. The decorator pattern refers to a class that
wraps another class, modifying its behavior.

In the context of a behavior tree, a decorator is a task that has one single child task and modifies its behavior in
some way. You could think of it like a composite task with a single child.

There are many types of decorators, such as AlwaysFail (failes no matter what), Limit (limit # times task can be run),
and others. See the wiki [link](https://github.com/libgdx/gdx-ai/wiki/Behavior-Trees#decorator) on decorators for more.

4) *Parallel*
A parallel is a special branch task that starts or resumes all children every single time. The parallel task will
succeed if all the children succeed, fail if one of the children fail.

One common use of the parallel task is continually check whether certain conditions are met while carrying out an
action. The typical use case: make the game entity react on event while sleeping or wandering.One common use of the
parallel task is continually check whether certain conditions are met while carrying out an action. The typical use
case: make the game entity react on event while sleeping or wandering.

Task Class Hierarchy

See "/Users/Allen/MEGA/Workspaces/game workspace/forwardstrategy/gdx-ai-task_class_hierarchy.png"


Using Data for Inter-Task Communication

To be effective the behavior tree API must allow tasks to share data with one another. The most sensible approach is to
decouple the data that behaviors need from the tasks themselves.The API does this by using an external data store for
all the data that the behavior tree needs. In AI literature such a store object is known as blackboard. Using this
external blackboard, we can write tasks that are still independent of one another but can communicate when needed.

NOTES==============================END GDX-AI INFO================================
 *
 * Seperate packages contain the various AI components created using gdx-ai library
 *
 *  Package containing classes that make up UnitAgent machine components of AI.
 * @see com.fs.game.ai.fsm#
 *   Tags below point to classes in gdx-ai extended/implemented in these classes
 * @see com.badlogic.gdx.ai#
 * @see com.badlogic.gdx.ai.fsm.State
 * @see com.badlogic.gdx.ai.msg.Telegraph
 * @see com.badlogic.gdx.ai.fsm.DefaultStateMachine
 * @see com.badlogic.gdx.ai.fsm.StateMachine
 * @see com.badlogic.gdx.ai.msg.MessageManager
 *
 *  Pathfinding package that implements IndexedAStartPathfinder methods with Telegraph
 * @see com.fs.game.ai.pf#
 *
 *
 *  Package containing BehaviorTree methods.
 * @see com.fs.game.ai.tasks#
 *
 *
 * NOTE on design:
 * Current Design:
 * AgentManager is the main class holding {@link com.badlogic.gdx.ai.msg.MessageDispatcher},
 *   {@link com.badlogic.gdx.ai.btree.BehaviorTree},
 *   {@link com.badlogic.gdx.ai.fsm.DefaultStateMachine}
 *   and {@link com.badlogic.gdx.ai.sched.Scheduler}.
 * An Array<UnitAgent> is created,
 * 2 Main BehaviorTreeLibraries are used: select.tree and act.tree
 *
 * UnitAgent is implements DefaultStateMachine, using AgentState enum to control Unit
 *
 *
 * 1) BehaviorTree selects
 *
 * Created by Allen on 5/6/15.
 *
 * @since 1.0
 * @author Allen
 * @package com.fs.game.ai
 */
package com.fs.game.ai;