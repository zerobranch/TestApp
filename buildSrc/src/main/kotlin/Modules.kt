sealed class Module(val root: String?, val name: String) {

    class Root(name: String) : Module(null, name) {
        companion object {
            val data = Root("data")
            val domain = Root("domain")
            val logging = Root("logging")
            val services = Root("services")
        }
    }

    class Presentation(name: String) : Module("presentation", name) {
        companion object {
            val home = Presentation("home")
            val training = Presentation("training")
            val settings = Presentation("settings")
            val backup = Presentation("backup")
            val auth = Presentation("auth")
            val addWordDialog = Presentation("add-word-dialog")
            val search = Presentation("search")
            val localSearch = Presentation("local-search")
            val lists = Presentation("lists")
            val categoryWordListDialog = Presentation("category-word-list-dialog")
            val typeWordListDialog = Presentation("type-word-list-dialog")
            val chooseCategoryDialog = Presentation("choose-category-dialog")
            val chooseTypeDialog = Presentation("choose-type-dialog")
            val simpleDialog = Presentation("simple-dialog")
            val categoryDialog = Presentation("category-dialog")
            val wordTypeDialog = Presentation("word-type-dialog")
            val wordGroupsDialog = Presentation("word-groups-dialog")
            val trainingSettingsDialog = Presentation("training-settings-dialog")
            val recentTrainingsDialog = Presentation("recent-trainings-dialog")

            val all: List<Presentation>
                get() = listOf(
                    home,
                    settings,
                    addWordDialog,
                    search,
                    localSearch,
                    lists,
                    categoryWordListDialog,
                    typeWordListDialog,
                    chooseCategoryDialog,
                    chooseTypeDialog,
                    simpleDialog,
                    categoryDialog,
                    wordTypeDialog,
                    wordGroupsDialog,
                    trainingSettingsDialog,
                    training,
                    recentTrainingsDialog,
                    backup,
                    auth,
                )
        }
    }

    class Commons(name: String) : Module("commons", name) {
        companion object {
            val java = Commons("commons-java")
            val android = Commons("commons-android")
            val items = Commons("commons-items")
            val app = Commons("commons-app")
            val view = Commons("commons-view")
        }
    }
}
