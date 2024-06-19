### Clases y Atributos ###

Clase: UserDto
  - id: String
  - registration: LocalDateTime
  - email: String
  - password: String
  - firstName: String
  - surNames: String
  - birthdate: LocalDate
  - roleType: Role
  - accountStatus: AccountStatus
  - requestDeleteDate: LocalDateTime

Clase: ActivationDto
  - id: String
  - activationCode: String
  - expiration: LocalDateTime

Clase: FavoriteDto
  - id: String
  - recipeIds: List<String>
  - ingredientIds: List<String>

Clase: ResetDto
  - id: String
  - code: String
  - expiration: LocalDateTime

Clase: CategoryDto
  - id: String
  - name: String

Clase: IngredientDto
  - id: String
  - name: String
  - quantity: Integer
  - measure: MeasureDto

Clase: MeasureDto
  - id: String
  - name: String

Clase: RecipeDto
  - id: String
  - name: String
  - preparation: String
  - portion: Double
  - categories: List<CategoryDto>
  - ingredients: List<IngredientDto>

### Relaciones ###

UserDto_id -> ActivationDto_id
UserDto_id -> FavoriteDto_id
UserDto_id -> ResetDto_id
RecipeDto_categories -> CategoryDto_id
RecipeDto_ingredients -> IngredientDto_id
IngredientDto_measure -> MeasureDto_id
FavoriteDto_recipeIds -> RecipeDto_id
FavoriteDto_ingredientIds -> IngredientDto_id
