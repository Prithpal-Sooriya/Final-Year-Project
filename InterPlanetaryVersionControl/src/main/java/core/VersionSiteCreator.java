/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

/**
 *
 * @author Prithpal
 */
public class VersionSiteCreator {

  public static final String IPFS_LOCALHOST = "http://localhost:8080/ipfs/";
  public static final String IPFS_HTTP_GATEWAY = "https://ipfs.io/ipfs/";
  public static final String IPNS_LOCALHOST = "http://localhost:8080/ipns/";
  public static final String IPNS_HTTP_GATEWAY = "https://ipfs.io/ipns/";

  private static String chosenOption = IPFS_LOCALHOST;

  private static final String INDEX_HTML
          = "<!DOCTYPE html>\n"
          + "<html lang=\"en\" dir=\"ltr\">\n"
          + "  <head>\n"
          + "    <meta charset=\"utf-8\">\n"
          + "    <title>Version Selector</title>\n"
          + "    <script src=\"https://code.jquery.com/jquery-3.3.1.min.js\"></script>\n"
          + "    <!-- <script type=\"text/javascript\" src=\".commits.json\"></script> -->\n"
          + "    <script src=\"displayCommits.js\"></script>\n"
          + "  </head>\n"
          + "  <body style=\"text-align: center\">\n"
          + "\n"
          + "    <div id=\"div_branches\">\n"
          + "      <h3>Branches</h3>\n"
          + "      <select id=\"select_branches\" onChange=\"changeBranch(this)\">\n"
          + "        <option hidden selected disabled>Select Branches</option>\n"
          + "      </select>\n"
          + "    </div>\n"
          + "    <hr>\n"
          + "    <h3 id=\"head_label\" style=\"display: none\">HEAD (MOST RECENT)</h3>\n"
          + "    <div id=\"head_commit_container\">\n"
          + "    </div>\n"
          + "    <h3 id=\"body_label\" style=\"display: none\">ALL VERSIONS</h3>\n"
          + "    <div id=\"commits_container\">\n"
          + "    </div>\n"
          + "  </body>\n"
          + "</html>\n";

  private static String javascript;
  
  private static void setJS(){
    javascript
          = "// let branchesObject[];\n"
          + "var branchObjects;\n"
          + "window.onload = function () {\n"
          + "  let jsonObject = function () {\n"
          + "    let jsonInner = null;\n"
          + "    $.ajax({\n"
          + "      \"async\": false,\n"
          + "      \"global\": false,\n"
          + "      \"url\": \".commits.json\",\n"
          + "      \"dataType\": \"json\",\n"
          + "      \"success\": function (data) {\n"
          + "        jsonInner = data;\n"
          + "      }\n"
          + "    });\n"
          + "    return jsonInner;\n"
          + "  }();\n"
          + "  branchObjects = jsonObject.branches;\n"
          + "  let branchNames = branchObjects.map(function (branchObject) {\n"
          + "    return Object.keys(branchObject)[0];\n"
          + "  });\n"
          + "  //append to select\n"
          + "  let selectBranches = document.getElementById(\"select_branches\");\n"
          + "  branchNames.forEach(function (branchName) {\n"
          + "    let option = document.createElement(\"option\");\n"
          + "    option.text = branchName;\n"
          + "    option.value = branchName;\n"
          + "    selectBranches.appendChild(option);\n"
          + "  });\n"
          + "};\n"
          + "\n"
          + "function changeBranch(select) {\n"
          + "  const branchName = select.options[select.selectedIndex].value;\n"
          + "  const branchObject =\n"
          + "          branchObjects.filter(function (branchObject) {\n"
          + "            return Object.keys(branchObject)[0] === branchName;\n"
          + "          }).map(function (branchObject) {\n"
          + "    return branchObject[branchName];\n"
          + "  })[0];\n"
          + "  \n"
          + "  //show head and commits\n"
          + "  //hide h3 labels for head and body\n"
          + "  document.getElementById(\"head_label\").style.display = \"block\";\n"
          + "  document.getElementById(\"body_label\").style.display = \"block\";\n"
          + "  \n"
          + "  //add head\n"
          + "  createVersions(branchObject.head, document.getElementById(\"head_commit_container\"));\n"
          + "  \n"
          + "  //add commits\n"
          + "  branchObject.versions.reverse().forEach(function(versionObject) {\n"
          + "    createVersions(versionObject, document.getElementById(\"commits_container\"))\n"
          + "  });\n"
          + "}\n"
          + "\n"
          + "function createVersions(branchObjectHead, container) {\n"
          + "  let versionContainer = document.createElement(\"div\");\n"
          + "  versionContainer.style.lineHeight = \"0%\";\n"
          + "  \n"
          + "  //date\n"
          + "  let dateH4 = document.createElement(\"h4\");\n"
          + "  let dateH4Strong = document.createElement(\"strong\");\n"
          + "  const date = new Date(branchObjectHead.date);\n"
          + "  const dateStr =\n"
          + "          date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear()\n"
          + "          + \" \" + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();\n"
          + "  dateH4.appendChild(\n"
          + "          dateH4Strong.appendChild(\n"
          + "                  document.createTextNode(dateStr)));\n"
          + "\n"
          + "  //commit message and date\n"
          + "  let p = document.createElement(\"p\");\n"
          + "  let i = document.createElement(\"i\");\n"
          + "  i.appendChild(document.createTextNode(branchObjectHead.author));\n"
          + "  p.appendChild(document.createTextNode(branchObjectHead.commitMessage + \" - \"));\n"
          + "  p.appendChild(i);\n"
          + "\n"
          + "  //hash\n"
          + "  let a = document.createElement(\"a\");\n"
          + "  a.href = \"" + chosenOption + "\" + branchObjectHead.hash;\n"
          + "  a.appendChild(document.createTextNode(\"Hash - \" + branchObjectHead.hash));\n"
          + "\n"
          + "  //combine\n"
          + "  versionContainer.appendChild(dateH4);\n"
          + "  versionContainer.appendChild(p);\n"
          + "  versionContainer.appendChild(a);\n"
          + "\n"
          + "  container.appendChild(versionContainer);\n"
          + "}\n";
  }
  
  public static String createHTML() {
    return INDEX_HTML;
  }

  public static String createJS(String option) {
    if (option.equals(IPFS_HTTP_GATEWAY) || option.equals(IPFS_LOCALHOST)
            || option.equals(IPNS_HTTP_GATEWAY) || option.equals(IPNS_LOCALHOST)) {
      chosenOption = option;
      setJS();
      return javascript;
    }
    System.err.println("VersionSiteCreator - createJS: was not one of the correct options ("
            + IPFS_HTTP_GATEWAY + '/' + IPFS_LOCALHOST + '/'
            + IPNS_HTTP_GATEWAY + '/' + IPNS_LOCALHOST + ')');
    return null;
  }
}
