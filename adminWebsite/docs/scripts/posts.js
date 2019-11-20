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
            <p id="postText" onload="contentInsert()"></p>
        </div>
        <div class="postActions">
          <button id="remove">Remove</button>
        </div>
    </div>
</div>

*/

var postStruct = '<div id="postsCont">'+'<div class="titleCont">'+'<div class="postTitle">'+'<p id="title">Title</p>'+'<p id="date">Time/Date</p>'+'</div>'+'</div>'+'<div class="content">'+'<div class="text">'+'<p id="postText"></p>'+'</div>'+'<div class="postActions">'+'<button id="remove" onclick="removePost()">Remove</button>'+'</div>'+'</div>'+'</div>';

var postContent = ["<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam ac ex quis nibh tempus posuere. Duis ullamcorper at libero sit amet semper. Aliquam ac hendrerit tellus. Integer non fermentum arcu. Cras pretium ut justo varius interdum. Donec vehicula, sapien id pharetra bibendum, dui justo fermentum mi, eu scelerisque felis nisl vitae metus. Donec dictum eget nunc non feugiat. Cras tempor id libero quis pharetra. In a est vulputate, pellentesque felis at, convallis nibh. Aliquam vitae odio nulla. Mauris id euismod justo, at vehicula odio. Etiam laoreet nibh quis aliquet convallis. Duis in eros elementum, iaculis lorem at, laoreet velit. Aliquam libero urna, rutrum posuere condimentum non, feugiat vitae massa.</p>"];

var posts = [];

document.getElementById("postCont").innerHTML = postStruct;

/*document.getElementById("postText").innerHTML = "Test";*/

/* Remove post function */
function removePost(){
    document.getElementById("postCont").innerHTML = "";
}

/*Text Insertion Function*/
function contentInsert(){
    /*
    for(var i = 0; i < postContent.size(); i++){
        document.getElementById("postText").innerHTML = postContent[i];
    }
    */
    document.getElementById("postText").innerHTML = "Test";
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