const links = [
    { "source": "UserDto", "target": "UserDto_id", "type": "attribute" },
    { "source": "UserDto", "target": "UserDto_registration", "type": "attribute" },
    { "source": "UserDto", "target": "UserDto_email", "type": "attribute" },
    { "source": "UserDto", "target": "UserDto_password", "type": "attribute" },
    { "source": "UserDto", "target": "UserDto_firstName", "type": "attribute" },
    { "source": "UserDto", "target": "UserDto_surNames", "type": "attribute" },
    { "source": "UserDto", "target": "UserDto_birthdate", "type": "attribute" },
    { "source": "UserDto", "target": "UserDto_roleType", "type": "attribute" },
    { "source": "UserDto", "target": "UserDto_accountStatus", "type": "attribute" },
    { "source": "UserDto", "target": "UserDto_requestDeleteDate", "type": "attribute" },
    { "source": "ActivationDto", "target": "ActivationDto_id", "type": "attribute" },
    { "source": "ActivationDto", "target": "ActivationDto_activationCode", "type": "attribute" },
    { "source": "ActivationDto", "target": "ActivationDto_expiration", "type": "attribute" },
    { "source": "FavoriteDto", "target": "FavoriteDto_id", "type": "attribute" },
    { "source": "FavoriteDto", "target": "FavoriteDto_recipeIds", "type": "attribute" },
    { "source": "FavoriteDto", "target": "FavoriteDto_ingredientIds", "type": "attribute" },
    { "source": "ResetDto", "target": "ResetDto_id", "type": "attribute" },
    { "source": "ResetDto", "target": "ResetDto_code", "type": "attribute" },
    { "source": "ResetDto", "target": "ResetDto_expiration", "type": "attribute" },
    { "source": "CategoryDto", "target": "CategoryDto_id", "type": "attribute" },
    { "source": "CategoryDto", "target": "CategoryDto_name", "type": "attribute" },
    { "source": "IngredientDto", "target": "IngredientDto_id", "type": "attribute" },
    { "source": "IngredientDto", "target": "IngredientDto_name", "type": "attribute" },
    { "source": "IngredientDto", "target": "IngredientDto_quantity", "type": "attribute" },
    { "source": "IngredientDto", "target": "IngredientDto_measure", "type": "attribute" },
    { "source": "MeasureDto", "target": "MeasureDto_id", "type": "attribute" },
    { "source": "MeasureDto", "target": "MeasureDto_name", "type": "attribute" },
    { "source": "RecipeDto", "target": "RecipeDto_id", "type": "attribute" },
    { "source": "RecipeDto", "target": "RecipeDto_name", "type": "attribute" },
    { "source": "RecipeDto", "target": "RecipeDto_preparation", "type": "attribute" },
    { "source": "RecipeDto", "target": "RecipeDto_portion", "type": "attribute" },
    { "source": "RecipeDto", "target": "RecipeDto_categories", "type": "attribute" },
    { "source": "RecipeDto", "target": "RecipeDto_ingredients", "type": "attribute" },
    { "source": "UserDto_id", "target": "ActivationDto_id", "type": "relation" },
    { "source": "UserDto_id", "target": "FavoriteDto_id", "type": "relation" },
    { "source": "UserDto_id", "target": "ResetDto_id", "type": "relation" },
    { "source": "RecipeDto_categories", "target": "CategoryDto_id", "type": "relation" },
    { "source": "RecipeDto_ingredients", "target": "IngredientDto_id", "type": "relation" },
    { "source": "IngredientDto_measure", "target": "MeasureDto_id", "type": "relation" },
    { "source": "FavoriteDto_recipeIds", "target": "RecipeDto_id", "type": "relation" },
    { "source": "FavoriteDto_ingredientIds", "target": "IngredientDto_id", "type": "relation" }
]


const nodes = [
    { "id": "UserDto", "label": "UserDto", "group": "user-app", "fx": 600, "fy": 200 },
    { "id": "ActivationDto", "label": "ActivationDto", "group": "user-app", "fx": 200, "fy": 400 },
    { "id": "FavoriteDto", "label": "FavoriteDto", "group": "user-app", "fx": 600, "fy": 600 },
    { "id": "ResetDto", "label": "ResetDto", "group": "user-app", "fx": 1000, "fy": 400 },
    { "id": "CategoryDto", "label": "CategoryDto", "group": "recipes-app", "fx": 1400, "fy": 200 },
    { "id": "IngredientDto", "label": "IngredientDto", "group": "recipes-app", "fx": 1400, "fy": 600 },
    { "id": "MeasureDto", "label": "MeasureDto", "group": "recipes-app", "fx": 1600, "fy": 400 },
    { "id": "RecipeDto", "label": "RecipeDto", "group": "recipes-app", "fx": 1000, "fy": 600 },
    { "id": "UserDto_id", "label": "id", "group": "user-app-attr", "fx": 500, "fy": 100 },
    { "id": "UserDto_registration", "label": "registration", "group": "user-app-attr", "fx": 550, "fy": 150 },
    { "id": "UserDto_email", "label": "email", "group": "user-app-attr", "fx": 650, "fy": 150 },
    { "id": "UserDto_password", "label": "password", "group": "user-app-attr", "fx": 700, "fy": 100 },
    { "id": "UserDto_firstName", "label": "firstName", "group": "user-app-attr", "fx": 500, "fy": 250 },
    { "id": "UserDto_surNames", "label": "surNames", "group": "user-app-attr", "fx": 550, "fy": 300 },
    { "id": "UserDto_birthdate", "label": "birthdate", "group": "user-app-attr", "fx": 650, "fy": 300 },
    { "id": "UserDto_roleType", "label": "roleType", "group": "user-app-attr", "fx": 700, "fy": 250 },
    { "id": "UserDto_accountStatus", "label": "accountStatus", "group": "user-app-attr", "fx": 800, "fy": 200 },
    { "id": "UserDto_requestDeleteDate", "label": "requestDeleteDate", "group": "user-app-attr", "fx": 800, "fy": 300 },
    { "id": "ActivationDto_id", "label": "id<userId>", "group": "user-app-attr", "fx": 100, "fy": 300 },
    { "id": "ActivationDto_activationCode", "label": "activationCode", "group": "user-app-attr", "fx": 150, "fy": 350 },
    { "id": "ActivationDto_expiration", "label": "expiration", "group": "user-app-attr", "fx": 250, "fy": 350 },
    { "id": "FavoriteDto_id", "label": "id<userId>", "group": "user-app-attr", "fx": 500, "fy": 500 },
    { "id": "FavoriteDto_recipeIds", "label": "List<recipeIds>", "group": "user-app-attr", "fx": 550, "fy": 550 },
    { "id": "FavoriteDto_ingredientIds", "label": "List<ingredientIds>", "group": "user-app-attr", "fx": 650, "fy": 550 },
    { "id": "ResetDto_id", "label": "id<userId>", "group": "user-app-attr", "fx": 900, "fy": 300 },
    { "id": "ResetDto_code", "label": "code", "group": "user-app-attr", "fx": 950, "fy": 350 },
    { "id": "ResetDto_expiration", "label": "expiration", "group": "user-app-attr", "fx": 1050, "fy": 350 },
    { "id": "CategoryDto_id", "label": "id", "group": "recipes-app-attr", "fx": 1300, "fy": 100 },
    { "id": "CategoryDto_name", "label": "name", "group": "recipes-app-attr", "fx": 1350, "fy": 150 },
    { "id": "IngredientDto_id", "label": "id", "group": "recipes-app-attr", "fx": 1300, "fy": 500 },
    { "id": "IngredientDto_name", "label": "name", "group": "recipes-app-attr", "fx": 1350, "fy": 550 },
    { "id": "IngredientDto_quantity", "label": "quantity", "group": "recipes-app-attr", "fx": 1450, "fy": 550 },
    { "id": "IngredientDto_measure", "label": "measure<MeasureDto>", "group": "recipes-app-attr", "fx": 1500, "fy": 500 },
    { "id": "MeasureDto_id", "label": "id", "group": "recipes-app-attr", "fx": 1550, "fy": 300 },
    { "id": "MeasureDto_name", "label": "name", "group": "recipes-app-attr", "fx": 1650, "fy": 350 },
    { "id": "RecipeDto_id", "label": "id", "group": "recipes-app-attr", "fx": 900, "fy": 500 },
    { "id": "RecipeDto_name", "label": "name", "group": "recipes-app-attr", "fx": 950, "fy": 550 },
    { "id": "RecipeDto_preparation", "label": "preparation", "group": "recipes-app-attr", "fx": 1050, "fy": 550 },
    { "id": "RecipeDto_portion", "label": "portion", "group": "recipes-app-attr", "fx": 1100, "fy": 500 },
    { "id": "RecipeDto_categories", "label": "List<CategoryDto>", "group": "recipes-app-attr", "fx": 1150, "fy": 450 },
    { "id": "RecipeDto_ingredients", "label": "List<IngredientDto>", "group": "recipes-app-attr", "fx": 1200, "fy": 500 }
]



var svg = d3.select("svg"),
    width = +svg.attr("width"),
    height = +svg.attr("height");

var simulation = d3.forceSimulation(nodes)
    .force("link", d3.forceLink(links).id(d => d.id).distance(100))
    .force("charge", d3.forceManyBody().strength(-500))
    .force("center", d3.forceCenter(width / 2, height / 2));

var link = svg.append("g")
    .attr("class", "links")
    .selectAll("line")
    .data(links)
    .enter().append("line")
    .attr("class", d => d.type === "relation" ? "link id-link" : "link");

var node = svg.append("g")
    .attr("class", "nodes")
    .selectAll("g")
    .data(nodes)
    .enter().append("g")
    .attr("class", "node")
    .call(d3.drag()
        .on("start", dragstarted)
        .on("drag", dragged)
        .on("end", dragended));

node.append("circle")
    .attr("r", 10)
    .attr("class", d => d.group);

node.append("text")
    .attr("x", 12)
    .attr("dy", ".35em")
    .text(d => d.label);

simulation.on("tick", () => {
    link
        .attr("x1", d => clamp(d.source.x, 0, width))
        .attr("y1", d => clamp(d.source.y, 0, height))
        .attr("x2", d => clamp(d.target.x, 0, width))
        .attr("y2", d => clamp(d.target.y, 0, height));

    node
        .attr("transform", d => `translate(${clamp(d.x, 0, width)},${clamp(d.y, 0, height)})`);
});

function dragstarted(event, d) {
    if (!event.active) simulation.alphaTarget(0.3).restart();
    d.fx = d.x;
    d.fy = d.y;
}

function dragged(event, d) {
    const dx = event.x - d.fx;
    const dy = event.y - d.fy;
    
    d.fx = event.x;
    d.fy = event.y;

    nodes.forEach(node => {
        if (node.id !== d.id && node.id.startsWith(d.id)) {
            node.fx += dx;
            node.fy += dy;
        }
    });
}

function dragended(event, d) {
    if (!event.active) simulation.alphaTarget(0);
    savePositions();
}

function clamp(x, lo, hi) {
    return x < lo ? lo : x > hi ? hi : x;
}

function savePositions() {
    const positions = nodes.map(node => ({
        id: node.id,
        fx: node.fx,
        fy: node.fy
    }));
    localStorage.setItem('nodePositions', JSON.stringify(positions));
}

function loadPositions() {
    const positions = JSON.parse(localStorage.getItem('nodePositions'));
    if (positions) {
        nodes.forEach(node => {
            const pos = positions.find(p => p.id === node.id);
            if (pos) {
                node.fx = pos.fx;
                node.fy = pos.fy;
            }
        });
    }
}

loadPositions();

