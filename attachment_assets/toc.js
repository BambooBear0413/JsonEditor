function generateToc() {
    let tocMenu = document.getElementById("table_of_contents");
    let titles = document.getElementsByClassName("title");
    let currentMenu = tocMenu;

    for(i = 0; i < titles.length; i++) {
        let title = titles[i];
        
        switch(title.tagName) {
            case "H2":
                if(currentMenu != tocMenu) {
                    currentMenu = tocMenu;
                }
                break;
            case "H3":
                if(currentMenu == tocMenu) {
                    currentMenu = document.createElement("ul");
                    currentMenu.className = "h2";
                    tocMenu.appendChild(currentMenu);
                } else if(currentMenu.className == "h3") {
                    currentMenu = currentMenu.parentNode;
                }
                break;
            case "H4":
                if(currentMenu.className != "h3") {
                    let parentMenu = currentMenu;
                    currentMenu = document.createElement("ul");
                    currentMenu.className = "h3";
                    parentMenu.appendChild(currentMenu);
                    break;
                }        
        }

        let listItem = document.createElement("li");
        listItem.innerHTML = `<a href="#${title.id}">${title.innerHTML}</a>`
        currentMenu.appendChild(listItem);
    }
}

window.onload = generateToc;