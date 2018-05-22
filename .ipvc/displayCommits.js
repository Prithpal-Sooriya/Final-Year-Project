//let branchesObject[];
var branchObjects;
window.onload = function () {
  let jsonObject = function () {
    let jsonInner = null;
    $.ajax({
      "async": false,
      "global": false,
      "url": ".commits.json",
      "dataType": "json",
      "success": function (data) {
        jsonInner = data;
      }
    });
    return jsonInner;
  }();
  branchObjects = jsonObject.branches;
  let branchNames = branchObjects.map(function (branchObject) {
    return Object.keys(branchObject)[0];
  });
  //append to select
  let selectBranches = document.getElementById("select_branches");
  branchNames.forEach(function (branchName) {
    let option = document.createElement("option");
    option.text = branchName;
    option.value = branchName;
    selectBranches.appendChild(option);
  });
};

function changeBranch(select) {
  const branchName = select.options[select.selectedIndex].value;
  const branchObject =
          branchObjects.filter(function (branchObject) {
            return Object.keys(branchObject)[0] === branchName;
          }).map(function (branchObject) {
    return branchObject[branchName];
  })[0];

  //show head and commits
  //hide h3 labels for head and body
  document.getElementById("head_label").style.display = "block";
  document.getElementById("body_label").style.display = "block";

  //remove children
  let head = document.getElementById("head_commit_container");
  while(head.firstChild) {
    head.removeChild(head.firstChild);
  }
  let body = document.getElementById("commits_container");
  while(body.firstChild) {
    body.removeChild(body.firstChild);
  }  //add head
  createVersions(branchObject.head, head);

  //add commits
  if(branchObject.head.hash == branchObject.versions[0].hash) {
    branchObject.versions.forEach(function(versionObject) {
      createVersions(versionObject, body)
    });
  }
  else {
    branchObject.versions.reverse().forEach(function(versionObject) {
      createVersions(versionObject, body)
    });
  }
}

function createVersions(branchObjectHead, container) {
  let versionContainer = document.createElement("div");
  versionContainer.style.lineHeight = "0%";

  //date
  let dateH4 = document.createElement("h4");
  let dateH4Strong = document.createElement("strong");
  const date = new Date(branchObjectHead.date);
  const dateStr =
          date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear()
          + " " + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
  dateH4.appendChild(
          dateH4Strong.appendChild(
                  document.createTextNode(dateStr)));

  //commit message and date
  let p = document.createElement("p");
  let i = document.createElement("i");
  i.appendChild(document.createTextNode(branchObjectHead.author));
  p.appendChild(document.createTextNode(branchObjectHead.commitMessage + " - "));
  p.appendChild(i);

  //hash
  let a = document.createElement("a");
  a.href = "http://localhost:8080/ipfs/" + branchObjectHead.hash;
  a.appendChild(document.createTextNode("Hash - " + branchObjectHead.hash));

  //combine
  versionContainer.appendChild(dateH4);
  versionContainer.appendChild(p);
  versionContainer.appendChild(a);

  container.appendChild(versionContainer);
}