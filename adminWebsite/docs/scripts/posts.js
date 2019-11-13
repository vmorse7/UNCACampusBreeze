/*
Pseudocode:
-----------
<div post>
  <div innerContainer>
    <title>
      call title from database
    </title>
    <date>
      call date and time
    </date>
    <hr>
    <content>
      call text from
    </content>
  </div innerContainer>
  <div controls>
    - remove
  </div controls>
</div post>

// Code in HTML
<div class="postsCont">
    <div class="titleCont">
        <div class="postTitle">
            <p id="title">Title</p>
            <p id="date">Time/Date</p>
        </div>
    </div>
    <div class="content">
        <div class="text">
            <p id="postText"></p>
        </div>
        <div class="postActions">
          <button id="remove">Remove</button>
        </div>
    </div>
</div>

*/

