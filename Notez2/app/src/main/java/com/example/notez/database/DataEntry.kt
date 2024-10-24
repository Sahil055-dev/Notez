package com.example.notez.database

class DatabaseInitializer(private val db: AppDatabase) {

    suspend fun initialize() {
        val branchDao = db.branchDao()
        val yearDao = db.yearDao()
        val semesterDao = db.semesterDao()
        val subjectDao = db.subjectDao()
        val moduleDao = db.moduleDao()
        val noteDao = db.curriculumNoteDao()
        val pastYearPaperDao = db.pastYearPaperDao()

        val userUploadedNoteDao = db.userUploadedNoteDao()


        // Example data

        // Insert Branches
        val branches = listOf(
            BranchEntity(name = "Computer Engineering"),
            BranchEntity(name = "Information Technology"),
            BranchEntity(name = "Mechanical Engineering"),
            BranchEntity(name = "Civil Engineering"),
            BranchEntity(name = "CSE-DS"),
            BranchEntity(name = "EXTC")
        )
        branches.forEach { branchDao.insertBranch(it) }

        // Retrieve branches to use names for other entities
        val branchComputerEngineering = branchDao.getBranch("Computer Engineering")!!
        val branchIT = branchDao.getBranch("Information Technology")!!
        val branchMechanical = branchDao.getBranch("Mechanical Engineering")!!
        val branchCivil = branchDao.getBranch("Civil Engineering")!!
        val branchCSE = branchDao.getBranch("CSE-DS")!!
        val branchEXTC = branchDao.getBranch("EXTC")!!

        // Insert Years
        val years = listOf(

            //Comps
            YearEntity(id = 1, branchName = branchComputerEngineering.name, yearNumber = 1),
            YearEntity(id = 2, branchName = branchComputerEngineering.name, yearNumber = 2),
            YearEntity(id = 3, branchName = branchComputerEngineering.name, yearNumber = 3),
            YearEntity(id = 4, branchName = branchComputerEngineering.name, yearNumber = 4),

            //Extc
            YearEntity(id = 5, branchName = branchEXTC.name, yearNumber = 1)
        )
        years.forEach { yearDao.insertYear(it) }

        // Retrieve years to use IDs for semesters
        //Comps
        val year1 = yearDao.getYears(branchComputerEngineering.name).find { it.yearNumber == 1 }!!
        val year2 = yearDao.getYears(branchComputerEngineering.name).find { it.yearNumber == 2 }!!
        val year3 = yearDao.getYears(branchComputerEngineering.name).find { it.yearNumber == 3 }!!
        val year4 = yearDao.getYears(branchComputerEngineering.name).find { it.yearNumber == 4 }!!

        //Extc
        val yearExtc1 = yearDao.getYears(branchEXTC.name).find { it.yearNumber == 1 }!!

        // Insert Semesters
        val semesters = listOf(
            SemesterEntity(id = 1, yearId = year1.id, semesterNumber = 1),
            SemesterEntity(id = 2, yearId = year1.id, semesterNumber = 2),
            SemesterEntity(id = 3, yearId = year2.id, semesterNumber = 3),
            SemesterEntity(id = 4, yearId = year2.id, semesterNumber = 4),
            SemesterEntity(id = 5, yearId = year3.id, semesterNumber = 5),
            SemesterEntity(id = 6, yearId = year3.id, semesterNumber = 6),
            SemesterEntity(id = 7, yearId = year4.id, semesterNumber = 7),
            SemesterEntity(id = 8, yearId = year4.id, semesterNumber = 8),
        )
        semesters.forEach { semesterDao.insertSemester(it) }

        // Retrieve semesters to use IDs for subjects
        val semester1 = semesterDao.getSemesters(year1.id).find { it.semesterNumber == 1 }!!
        val semester2 = semesterDao.getSemesters(year1.id).find { it.semesterNumber == 2 }!!
        val semester3 = semesterDao.getSemesters(year2.id).find { it.semesterNumber == 3 }!!
        val semester4 = semesterDao.getSemesters(year2.id).find { it.semesterNumber == 4 }!!
        val semester5 = semesterDao.getSemesters(year3.id).find { it.semesterNumber == 5 }!!
        val semester6 = semesterDao.getSemesters(year3.id).find { it.semesterNumber == 6 }!!
        val semester7 = semesterDao.getSemesters(year4.id).find { it.semesterNumber == 7 }!!
        val semester8 = semesterDao.getSemesters(year4.id).find { it.semesterNumber == 8 }!!

        // Insert Subjects
        val subjects = listOf(
            //
            //Sem 1
            SubjectEntity(id = 1, name = "Maths-1", semesterId = semester1.id, description = "Engineering Mathematics-1"),
            SubjectEntity(id = 2, name = "Physics-1", semesterId = semester1.id, description = "Engineering Physics-1"),
            SubjectEntity(id = 3, name = "Chemistry-1", semesterId = semester1.id, description = "Engineering Chemistry-1"),
            SubjectEntity(id = 4, name = "Mechanics", semesterId = semester1.id, description = "Engineering Mechanics"),
            SubjectEntity(id = 5, name = "BEE", semesterId = semester1.id, description = "Basic Electrical Engineering"),

            //Sem 2
            SubjectEntity(id = 6, name = "Maths-2", semesterId = semester2.id, description = "Engineering Mathematics-2"),
            SubjectEntity(id = 7, name = "Physics-2", semesterId = semester2.id, description = "Engineering Physics- 2"),
            SubjectEntity(id = 8, name = "Chemistry-2", semesterId = semester2.id, description = "Engineering Chemistry- 2"),
            SubjectEntity(id = 9, name = "C-Prog", semesterId = semester2.id, description = "C-Programming"),
            SubjectEntity(id = 10, name = "Engineering Drawing", semesterId = semester2.id, description = "Engineering Graphics"),
            SubjectEntity(id = 11, name = "PCE-1", semesterId = semester2.id, description = "Professional Communication and Ethics- 1"),

            //Sem3
            SubjectEntity(id = 12, name = "Maths-3", semesterId = semester3.id, description = "Engineering Mathematics-3"),
            SubjectEntity(id = 13 , name = "Discrete Structure and Graph Theory", semesterId = semester3.id, description = "Discrete Structure and Graph Theory"),
            SubjectEntity(id = 14 , name = "Data Structures", semesterId = semester3.id, description = "Data Structures"),
            SubjectEntity(id = 15, name = "Digital Logic and Computer Architecture", semesterId = semester3.id, description = "Digital Logic and Computer Architecture"),
            SubjectEntity(id = 16, name = "Computer Graphics", semesterId = semester3.id, description = "Computer Graphics"),

            //Sem4
            SubjectEntity(id = 18, name = "", semesterId = semester4.id, description = ""),
            SubjectEntity(id = 19, name = "", semesterId = semester4.id, description = ""),
            SubjectEntity(id = 20, name = "", semesterId = semester4.id, description = ""),
            SubjectEntity(id = 21, name = "", semesterId = semester4.id, description = ""),
            SubjectEntity(id = 22, name = "", semesterId = semester4.id, description = ""),
            SubjectEntity(id = 23, name = "", semesterId = semester4.id, description = ""),

            //Sem 5
            SubjectEntity(id = 24 , name = "", semesterId = semester5.id, description = ""),
            SubjectEntity(id = 25, name = "", semesterId = semester5.id, description = ""),
            SubjectEntity(id = 26, name = "", semesterId = semester5.id, description = ""),
            SubjectEntity(id = 27, name = "", semesterId = semester5.id, description = ""),
            SubjectEntity(id = 28, name = "", semesterId = semester5.id, description = ""),
            SubjectEntity(id = 29, name = "", semesterId = semester5.id, description = ""),

            //Sem 6
            SubjectEntity(id = 30, name = "", semesterId = semester6.id, description = ""),
            SubjectEntity(id = 31, name = "", semesterId = semester6.id, description = ""),
            SubjectEntity(id = 32, name = "", semesterId = semester6.id, description = ""),
            SubjectEntity(id = 33, name = "", semesterId = semester6.id, description = ""),
            SubjectEntity(id = 34, name = "", semesterId = semester6.id, description = ""),
            SubjectEntity(id = 35, name = "", semesterId = semester6.id, description = ""),

            //Sem 7
            SubjectEntity(id = 36, name = "", semesterId = semester7.id, description = ""),
            SubjectEntity(id = 37, name = "", semesterId = semester7.id, description = ""),
            SubjectEntity(id = 38, name = "", semesterId = semester7.id, description = ""),
            SubjectEntity(id = 39, name = "", semesterId = semester7.id, description = ""),
            SubjectEntity(id = 40, name = "", semesterId = semester7.id, description = ""),
            SubjectEntity(id = 41, name = "", semesterId = semester7.id, description = ""),

            //Sem 8
            SubjectEntity(id = 42, name = "", semesterId = semester8.id, description = ""),
            SubjectEntity(id = 43, name = "", semesterId = semester8.id, description = ""),
            SubjectEntity(id = 44, name = "", semesterId = semester8.id, description = ""),
            SubjectEntity(id = 45, name = "", semesterId = semester8.id, description = ""),
            SubjectEntity(id = 46, name = "", semesterId = semester8.id, description = ""),
            SubjectEntity(id = 47, name = "", semesterId = semester8.id, description = ""),
        )
        subjects.forEach { subjectDao.insertSubject(it) }

// Retrieve subjects to use IDs for modules, notes, and PYQs
        //Sem 1
        val subjectMaths1 = subjectDao.getSubjects(semester1.id).find { it.name == "Maths-1" }!!
        val subjectPhysics1 = subjectDao.getSubjects(semester1.id).find { it.name == "Physics-1" }!!
        val subjectChemistry1 = subjectDao.getSubjects(semester1.id).find { it.name == "Chemistry-1" }!!
        val subjectMechanics = subjectDao.getSubjects(semester1.id).find { it.name == "Mechanics" }!!
        val subjectBEE = subjectDao.getSubjects(semester1.id).find { it.name == "BEE" }!!

        //Sem2
        val subjectMaths2 = subjectDao.getSubjects(semester2.id).find { it.name == "Maths-2" }!!
        val subjectPhysics2 = subjectDao.getSubjects(semester2.id).find { it.name == "Physics-2" }!!
        val subjectChemisrty2 = subjectDao.getSubjects(semester2.id).find { it.name == "Chemistry-2" }!!
        val subjectCprog = subjectDao.getSubjects(semester2.id).find { it.name == "C-Prog" }!!
        val subjectEG = subjectDao.getSubjects(semester2.id).find { it.name == "Engineering Drawing" }!!
        val subjectPce = subjectDao.getSubjects(semester2.id).find { it.name =="PCE-1" }

        //Sem 3
        val subjectMaths3 = subjectDao.getSubjects(semester3.id).find { it.name == "Maths-3" }!!
        val subjectDsgt = subjectDao.getSubjects(semester3.id).find { it.name == "Discrete Structure and Graph Theory" }!!
        val subjectDS = subjectDao.getSubjects(semester3.id).find { it.name == "Data Structures" }!!
        val subjectDlca = subjectDao.getSubjects(semester3.id).find { it.name == "Digital Logic and Computer Architecture" }!!
        val subjectCG = subjectDao.getSubjects(semester3.id).find { it.name == "Computer Graphics" }!!


        // Insert Modules
        val modules = listOf(
            //SEM 1
            ModuleEntity(id = 1, subjectId = subjectMaths1.id, moduleName = "Complex Numbers"),
            ModuleEntity(id = 2, subjectId = subjectMaths1.id, moduleName = "Hyperbolic Functions"),
            ModuleEntity(id = 3, subjectId = subjectMaths1.id, moduleName = "Partial Differentiation"),
            ModuleEntity(id = 4, subjectId = subjectMaths1.id, moduleName = "Application of Partial Differentiation"),
            ModuleEntity(id = 5, subjectId = subjectMaths1.id, moduleName = "Matrices"),
            ModuleEntity(id = 6, subjectId = subjectMaths1.id, moduleName = "Numerical Solutions"),
            //SEM 2
            ModuleEntity(id = 7, subjectId = subjectMaths2.id, moduleName = "Complex Numbers"),
            ModuleEntity(id = 8, subjectId = subjectPhysics2.id, moduleName = "Module 1"),
        )
        modules.forEach { moduleDao.insertModule(it) }

        // Insert Notes
        val notes = listOf(
            CurriculumNoteEntity(id = 1, moduleId = modules[0].id, title = "Note 1", contentUrl = "url/to/note1"),
            CurriculumNoteEntity(id = 2, moduleId = modules[1].id, title = "Note 2", contentUrl = "url/to/note2")
        )
        notes.forEach { noteDao.insertNote(it) }

        val pastYearPapers = listOf(
            //Maths1
            PastYearPaperEntity(id = 1, subjectId = subjectMaths1.id, year = 2024, month = "May", pdfUrl = "1/Maths1/MATHS1-MAY24.pdf"),
            PastYearPaperEntity(id = 2, subjectId = subjectMaths1.id, year = 2023, month = "December", pdfUrl = "1/Maths1/MATHS1-DEC23.pdf"),
            PastYearPaperEntity(id = 3, subjectId = subjectMaths1.id, year = 2023, month = "May", pdfUrl = "1/Maths1/Maths1-May23.pdf"),
            PastYearPaperEntity(id = 4, subjectId = subjectMaths1.id, year = 2022, month = "December", pdfUrl = "1/Maths1/Maths1-Dec22.pdf"),
            PastYearPaperEntity(id = 5, subjectId = subjectMaths1.id, year = 2022, month = "May", pdfUrl = "1/Maths1/Maths1-May22.pdf"),

            //Physics 1
            PastYearPaperEntity(id = 6, subjectId = subjectPhysics1.id, year = 2024, month = "May", pdfUrl = "1/Physics1/phy1-may24.pdf"),
            PastYearPaperEntity(id = 7, subjectId = subjectPhysics1.id, year = 2023, month = "December", pdfUrl = "1/Physics1/phy1-dec23.pdf"),
            PastYearPaperEntity(id = 8, subjectId = subjectPhysics1.id, year = 2023, month = "May", pdfUrl = "1/Physics1/phy1-may23.pdf"),
            PastYearPaperEntity(id = 9, subjectId = subjectPhysics1.id, year = 2022, month = "December", pdfUrl = "1/Physics1/phy1-dec22.pdf"),
            PastYearPaperEntity(id = 10, subjectId = subjectPhysics1.id, year = 2022, month = "May", pdfUrl = "1/Physics1/phy1-may22.pdf"),

            //Chemistry 1
            PastYearPaperEntity(id = 11, subjectId = subjectChemistry1.id, year = 2024, month = "May", pdfUrl = "1/Chem1/chem1-may24.pdf"),
            PastYearPaperEntity(id = 12, subjectId = subjectChemistry1.id, year = 2023, month = "December", pdfUrl = "1/Chem1/chem1-dec23.pdf"),
            PastYearPaperEntity(id = 13, subjectId = subjectChemistry1.id, year = 2023, month = "May", pdfUrl = "1/Chem1/chem1-may23.pdf"),
            PastYearPaperEntity(id = 14, subjectId = subjectChemistry1.id, year = 2022, month = "December", pdfUrl = "1/Chem1/chem1-dec22.pdf"),
            PastYearPaperEntity(id = 15, subjectId = subjectChemistry1.id, year = 2022, month = "May", pdfUrl = "1/Chem1/chem1-may22.pdf"),

            )

        pastYearPapers.forEach { pastYearPaperDao.insertPastYearPaper(it) }





        //

        val userUploadedNotes = listOf(
            UserUploadedNoteEntity(id = 1, moduleId = modules[0].id, title = "User Note 1", contentUrl = "url/to/usernote1"),
            UserUploadedNoteEntity(id = 2, moduleId = modules[1].id, title = "User Note 2", contentUrl = "url/to/usernote2")
        )
        userUploadedNotes.forEach { userUploadedNoteDao.insertUserUploadedNote(it) }

    }
}