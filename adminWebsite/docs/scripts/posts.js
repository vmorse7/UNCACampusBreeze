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

var postStruct = '<div id="postsCont">'+'<div class="titleCont">'+'<div class="postTitle">'+'<p id="title">Title</p>'+'<p id="date">Time/Date</p>'+'</div>'+'</div>'+'<div class="content">'+'<div class="text">'+'<p id="postText"></p>'+'</div>'+'<div class="postActions">'+'<button id="remove" onclick="removePost()">Remove</button>'+'</div>'+'</div>'+'</div>';

document.getElementById("postCont").innerHTML = postStruct;

/*Remove post function */
function removePost(){
    document.getElementById("postCont").innerHTML = "";
}

/*
function loadPosts(){
    document.getElementById("postCont").innerHTML = postStruct;
    
    
    
  var postStruct = '<div id="postsCont">'+'<div class="titleCont">'+'<div class="postTitle">'+'<p id="title">Title</p>'+'<p id="date">Time/Date</p>'+'</div>'+'</div>'+'<div class="content">'+'<div class="text">'+'<p id="postText"></p>'+'</div>'+'<div class="postActions">'+'<button id="remove">Remove</button>'+'</div>'+'</div>'+'</div>';
  var postListLength = 9;
  var postName = "Wahoo";
  var postDate = "10-15-20";
  var postText = "Yahoo";
  
    for(i = 0; i < 4; i++){
        
        
        
    }
  
}*/