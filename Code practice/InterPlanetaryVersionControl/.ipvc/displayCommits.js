// let branchesObject[];
var branchObjects;
window.onload = function() {
  let jsonObject = function() {
    let jsonInner = null;
    $.ajax({
      "async":false,
      "global":false,
      "url": "/commits.json",
      "dataType": "json",
      "success": function (data) {
        jsonInner = data;
      }
    });
    return jsonInner;
  }();
  branchObjects = jsonObject.branches;
  branchNames = branchObjects.map(function(x){return "hello"});
}

// let object = JSON.parse(.commits.json);
//
// let select_branches = document.getElementById("select_branches");
// let branchesObjects = object.branches;
// let branchNames = branchObjects;
// console.log(branchObject[0]);
