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
        val yearExtc1 = yearDao.getYears(branchComputerEngineering.name).find { it.yearNumber == 1 }!!

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
            SubjectEntity(id = 18, name = "Maths-4", semesterId = semester4.id, description = "Engineering Mathematics IV"),
            SubjectEntity(id = 19, name = "Analysis Of Algorithm", semesterId = semester4.id, description = "Analysis of Algorithm "),
            SubjectEntity(id = 20, name = "DBMS", semesterId = semester4.id, description = "Database Management System"),
            SubjectEntity(id = 21, name = "Operating System", semesterId = semester4.id, description = "Operating System"),
            SubjectEntity(id = 22, name = "MicroProcessor", semesterId = semester4.id, description = "Microprocessor"),


            //Sem 5
            SubjectEntity(id = 24 , name = "Theoretical Computer Science", semesterId = semester5.id, description = "Theoretical Computer Science"),
            SubjectEntity(id = 25, name = "Software Engg", semesterId = semester5.id, description = "Software Engineering"),
            SubjectEntity(id = 26, name = "Computer Network", semesterId = semester5.id, description = "Computer Network"),
            SubjectEntity(id = 27, name = "DWM", semesterId = semester5.id, description = "Data Warehouse & Mining"),

            //Sem 6
            SubjectEntity(id = 30, name = "SPCC", semesterId = semester6.id, description = "System Programming & Compiler Construction"),
            SubjectEntity(id = 31, name = "CryptoGraphy", semesterId = semester6.id, description = "Cryptography & System Security"),
            SubjectEntity(id = 32, name = "Mobile Computing", semesterId = semester6.id, description = "Mobile Computing"),
            SubjectEntity(id = 33, name = "A.I.", semesterId = semester6.id, description = "Artificial Intelligence"),

            //Sem 7
            SubjectEntity(id = 36, name = "M.L.", semesterId = semester7.id, description = "Machine Learning"),
            SubjectEntity(id = 37, name = "Big Data Analysis", semesterId = semester7.id, description = "Big Data Analysis"),


            //Sem 8
            SubjectEntity(id = 42, name = "Distributed Computing", semesterId = semester8.id, description = ""),

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
        val subjectChemistry2 = subjectDao.getSubjects(semester2.id).find { it.name == "Chemistry-2" }!!
        val subjectCprog = subjectDao.getSubjects(semester2.id).find { it.name == "C-Prog" }!!
        val subjectEG = subjectDao.getSubjects(semester2.id).find { it.name == "Engineering Drawing" }!!
        val subjectPce = subjectDao.getSubjects(semester2.id).find { it.name =="PCE-1" }!!

        //Sem 3
        val subjectMaths3 = subjectDao.getSubjects(semester3.id).find { it.name == "Maths-3" }!!
        val subjectDsgt = subjectDao.getSubjects(semester3.id).find { it.name == "Discrete Structure and Graph Theory" }!!
        val subjectDS = subjectDao.getSubjects(semester3.id).find { it.name == "Data Structures" }!!
        val subjectDlca = subjectDao.getSubjects(semester3.id).find { it.name == "Digital Logic and Computer Architecture" }!!
        val subjectCG = subjectDao.getSubjects(semester3.id).find { it.name == "Computer Graphics" }!!

        //SEM 4
        val subjectMaths4 = subjectDao.getSubjects(semester4.id).find { it.name == "Maths-4" }!!
        val subjectAOA = subjectDao.getSubjects(semester4.id).find { it.name == "Analysis Of Algorithm" }!!
        val subjectDBMS = subjectDao.getSubjects(semester4.id).find { it.name == "DBMS" }!!
        val subjectOS = subjectDao.getSubjects(semester4.id).find { it.name == "Operating System" }!!
        val subjectMP = subjectDao.getSubjects(semester4.id).find { it.name == "MicroProcessor" }!!

        //SEM 5
        val subjectTCS = subjectDao.getSubjects(semester5.id).find { it.name == "Theoretical Computer Science" }!!
        val subjectSE = subjectDao.getSubjects(semester5.id).find { it.name == "Software Engg" }!!
        val subjectCN = subjectDao.getSubjects(semester5.id).find { it.name == "Computer Network" }!!
        val subjectDWM = subjectDao.getSubjects(semester5.id).find { it.name == "DWM" }!!


        // Insert Modules
        val modules = listOf(
            //SEM 1
            //Maths 1
            ModuleEntity(id = 1, subjectId = subjectMaths1.id, moduleName = "Complex Numbers"),
            ModuleEntity(id = 2, subjectId = subjectMaths1.id, moduleName = "Hyperbolic Functions"),
            ModuleEntity(id = 3, subjectId = subjectMaths1.id, moduleName = "Partial Differentiation"),
            ModuleEntity(id = 4, subjectId = subjectMaths1.id, moduleName = "Application of Partial Differentiation"),
            ModuleEntity(id = 5, subjectId = subjectMaths1.id, moduleName = "Matrices"),
            ModuleEntity(id = 6, subjectId = subjectMaths1.id, moduleName = "Numerical Solutions"),
            //Physics
            ModuleEntity(id = 7, subjectId = subjectPhysics1.id, moduleName = "Quantum Physics"),
            ModuleEntity(id = 8, subjectId = subjectPhysics1.id, moduleName = "Crystallography"),
            ModuleEntity(id = 10, subjectId = subjectPhysics1.id, moduleName = "Semiconductors"),
            ModuleEntity(id = 11, subjectId = subjectPhysics1.id, moduleName = "Interference in thin film"),
            ModuleEntity(id = 12, subjectId = subjectPhysics1.id, moduleName = "Superconductors & Supercapacitors"),
            ModuleEntity(id = 13, subjectId = subjectPhysics1.id, moduleName = "Engginering Materials & Application"),
            //Chemistry
            ModuleEntity(id = 14, subjectId = subjectChemistry1.id, moduleName = "Atomic & Molecular Structure"),
            ModuleEntity(id = 15, subjectId = subjectChemistry1.id, moduleName = "Aromatic system & their molecular structure"),
            ModuleEntity(id = 16, subjectId = subjectChemistry1.id, moduleName = "Intermolecular Forces"),
            ModuleEntity(id = 17, subjectId = subjectChemistry1.id, moduleName = "Phase Rule"),
            ModuleEntity(id = 18, subjectId = subjectChemistry1.id, moduleName = "Polymers"),
            ModuleEntity(id = 19, subjectId = subjectChemistry1.id, moduleName = "Water"),
            //Mechanics
            ModuleEntity(id = 20, subjectId = subjectMechanics.id, moduleName = "Coplanar Forces & Resultant"),
            ModuleEntity(id = 21, subjectId = subjectMechanics.id, moduleName = "Equilibrium"),
            ModuleEntity(id = 22, subjectId = subjectMechanics.id, moduleName = "Friction"),
            ModuleEntity(id = 23, subjectId = subjectMechanics.id, moduleName = "Kinematics"),
            ModuleEntity(id = 24, subjectId = subjectMechanics.id, moduleName = "Kinematics of Rigid body"),
            ModuleEntity(id = 25, subjectId = subjectMechanics.id, moduleName = "Kinetics"),
            //BEE
            ModuleEntity(id = 26, subjectId = subjectBEE.id, moduleName = "DC Circuits"),
            ModuleEntity(id = 27, subjectId = subjectBEE.id, moduleName = "AC Circuits"),
            ModuleEntity(id = 28, subjectId = subjectBEE.id, moduleName = "Three Phase Voltages"),
            ModuleEntity(id = 29, subjectId = subjectBEE.id, moduleName = "Transformers"),
            ModuleEntity(id = 30, subjectId = subjectBEE.id, moduleName = "Electrical Machines"),
            ModuleEntity(id = 31, subjectId = subjectBEE.id, moduleName = "Induction Motors"),

            //SEMESTER 2
            //MATHS-2
            ModuleEntity(id = 32, subjectId = subjectMaths2.id, moduleName = "Differential Equations of First Order and First Degree"),
            ModuleEntity(id = 33, subjectId = subjectMaths2.id, moduleName = "Linear Differential Equations"),
            ModuleEntity(id = 34, subjectId = subjectMaths2.id, moduleName = "Beta and Gamma Function"),
            ModuleEntity(id = 35, subjectId = subjectMaths2.id, moduleName = "Multiple Integration-1"),
            ModuleEntity(id = 36, subjectId = subjectMaths2.id, moduleName = "Multiple Integration-2"),
            ModuleEntity(id = 37, subjectId = subjectMaths2.id, moduleName = "Numerical solution"),
            //PHYSICS 2
            ModuleEntity(id = 38, subjectId = subjectPhysics2.id, moduleName = "DIFFRACTION"),
            ModuleEntity(id = 39, subjectId = subjectPhysics2.id, moduleName = "LASER AND FIBRE OPTICS"),
            ModuleEntity(id = 40, subjectId = subjectPhysics2.id, moduleName = "ELECTRODYNAMICS"),
            ModuleEntity(id = 41, subjectId = subjectPhysics2.id, moduleName = "RELATIVITY"),
            ModuleEntity(id = 42, subjectId = subjectPhysics2.id, moduleName = "NANOTECHNOLOGY"),
            ModuleEntity(id = 43, subjectId = subjectPhysics2.id, moduleName = "PHYSICS OF SENSORS"),
            // CHEMISTRY 2
            ModuleEntity(id = 44, subjectId = subjectChemistry2.id, moduleName = "Principles of Spectroscopy"),
            ModuleEntity(id = 45, subjectId = subjectChemistry2.id, moduleName = "Applications of Spectroscopy"),
            ModuleEntity(id = 46, subjectId = subjectChemistry2.id, moduleName = "Concept of Electrochemistry"),
            ModuleEntity(id = 47, subjectId = subjectChemistry2.id, moduleName = "Corrosion"),
            ModuleEntity(id = 48, subjectId = subjectChemistry2.id, moduleName = "Green Chemistry and Synthesis of drugs"),
            ModuleEntity(id = 49, subjectId = subjectChemistry2.id, moduleName = "Fuels and Combustion"),
            //EG
            ModuleEntity(id = 50, subjectId = subjectEG.id, moduleName = "Introduction to Engineering Graphics"),
            ModuleEntity(id = 51, subjectId = subjectEG.id, moduleName = "Projection of Points and Lines"),
            ModuleEntity(id = 52, subjectId = subjectEG.id, moduleName = "Projection of Solids"),
            ModuleEntity(id = 53, subjectId = subjectEG.id, moduleName = "Section of Solids"),
            ModuleEntity(id = 54, subjectId = subjectEG.id, moduleName = "Orthographic and Sectional Orthographic Projections:"),
            ModuleEntity(id = 55, subjectId = subjectEG.id, moduleName = "Isometric Views"),
            //C-Prog
            ModuleEntity(id = 56, subjectId = subjectCprog.id, moduleName = "Fundamentals of C Programming"),
            ModuleEntity(id = 57, subjectId = subjectCprog.id, moduleName = "Control Structures"),
            ModuleEntity(id = 58, subjectId = subjectCprog.id, moduleName = "Functions"),
            ModuleEntity(id = 59, subjectId = subjectCprog.id, moduleName = "Arrays and Strings"),
            ModuleEntity(id = 60, subjectId = subjectCprog.id, moduleName = "Structure and Union"),
            ModuleEntity(id = 61, subjectId = subjectCprog.id, moduleName = "Pointers"),
            //PCE
            ModuleEntity(id = 62, subjectId = subjectPce.id, moduleName = "FUNDAMENTALS OF COMMUNICATION"),
            ModuleEntity(id = 63, subjectId = subjectPce.id, moduleName = "VERBAL APTITUDE FOR EMPLOYMENT"),
            ModuleEntity(id = 64, subjectId = subjectPce.id, moduleName = "DEVELOPING READING AND WRITING SKILLS"),
            ModuleEntity(id = 65, subjectId = subjectPce.id, moduleName = "BUSINESS CORRESPONDENCE"),
            ModuleEntity(id = 66, subjectId = subjectPce.id, moduleName = "BASIC TECHNICAL WRITING"),
            ModuleEntity(id = 67, subjectId = subjectPce.id, moduleName = "PERSONALITY DEVELOPMENT AND SOCIAL ETIQUETTES"),

            //SEMESTER 3
            //Maths 3
            ModuleEntity(id = 68, subjectId = subjectMaths3.id, moduleName = "Laplace Transform"),
            ModuleEntity(id = 69, subjectId = subjectMaths3.id, moduleName = "Inverse Laplace Transform"),
            ModuleEntity(id = 70, subjectId = subjectMaths3.id, moduleName = "Fourier Series"),
            ModuleEntity(id = 71, subjectId = subjectMaths3.id, moduleName = "Complex Variables"),
            ModuleEntity(id = 72, subjectId = subjectMaths3.id, moduleName = "Statistical Techniques"),
            ModuleEntity(id = 73, subjectId = subjectMaths3.id, moduleName = "Probability"),
            //DSGT
            ModuleEntity(id = 74, subjectId = subjectDsgt.id, moduleName = "Logic"),
            ModuleEntity(id = 75, subjectId = subjectDsgt.id, moduleName = "Relations and Functions"),
            ModuleEntity(id = 76, subjectId = subjectDsgt.id, moduleName = "Posets and Lattice"),
            ModuleEntity(id = 77, subjectId = subjectDsgt.id, moduleName = "Counting"),
            ModuleEntity(id = 78, subjectId = subjectDsgt.id, moduleName = "Algebraic Structures"),
            ModuleEntity(id = 79, subjectId = subjectDsgt.id, moduleName = "Graph Theory"),
            //DS
            ModuleEntity(id = 80, subjectId = subjectDS.id, moduleName = "Introduction to Data Structures"),
            ModuleEntity(id = 81, subjectId = subjectDS.id, moduleName = "Stack and Queues"),
            ModuleEntity(id = 82, subjectId = subjectDS.id, moduleName = "Linked List"),
            ModuleEntity(id = 83, subjectId = subjectDS.id, moduleName = "Trees"),
            ModuleEntity(id = 84, subjectId = subjectDS.id, moduleName = "Graphs"),
            ModuleEntity(id = 85, subjectId = subjectDS.id, moduleName = "Searching Techniques"),
            //DLCOA
            ModuleEntity(id = 86, subjectId = subjectDlca.id, moduleName = "Computer Fundamentals"),
            ModuleEntity(id = 87, subjectId = subjectDlca.id, moduleName = "Data Representation and Arithmetic algorithms"),
            ModuleEntity(id = 88, subjectId = subjectDlca.id, moduleName = "Processor Organization and Architecture"),
            ModuleEntity(id = 89, subjectId = subjectDlca.id, moduleName = "Control Unit Design"),
            ModuleEntity(id = 90, subjectId = subjectDlca.id, moduleName = "Memory Organization"),
            ModuleEntity(id = 91, subjectId = subjectDlca.id, moduleName = "Principles of Advanced Processor and Buses"),
            //CG
            ModuleEntity(id = 92, subjectId = subjectCG.id, moduleName = "Introduction and Overview of Graphics System"),
            ModuleEntity(id = 93, subjectId = subjectCG.id, moduleName = "Output Primitives"),
            ModuleEntity(id = 94, subjectId = subjectCG.id, moduleName = "Two Dimensional Geometric Transformations"),
            ModuleEntity(id = 95, subjectId = subjectCG.id, moduleName = "Two-Dimensional Viewing and Clipping"),
            ModuleEntity(id = 96, subjectId = subjectCG.id, moduleName = "Three Dimensional Geometric Transformations"),
            ModuleEntity(id = 97, subjectId = subjectCG.id, moduleName = "Visible Surface Detection and Animation"),

            //SEMESTER 4
            //MATHS 4
            ModuleEntity(id = 98, subjectId = subjectMaths4.id, moduleName = "Linear Algebra"),
            ModuleEntity(id = 99, subjectId = subjectMaths4.id, moduleName = "Complex Integration"),
            ModuleEntity(id = 100, subjectId = subjectMaths4.id, moduleName = "Z Transform"),
            ModuleEntity(id = 101, subjectId = subjectMaths4.id, moduleName = "Probability Distribution and Sampling Theory"),
            ModuleEntity(id = 102, subjectId = subjectMaths4.id, moduleName = "Linear Programming Problems"),
            ModuleEntity(id = 103, subjectId = subjectMaths4.id, moduleName = "Nonlinear Programming Problems"),
            //AOA
            ModuleEntity(id = 104, subjectId = subjectAOA.id, moduleName = "Introduction"),
            ModuleEntity(id = 105, subjectId = subjectAOA.id, moduleName = "Divide and Conquer Approach"),
            ModuleEntity(id = 106, subjectId = subjectAOA.id, moduleName = "Greedy Method Approach"),
            ModuleEntity(id = 107, subjectId = subjectAOA.id, moduleName = "Dynamic Programming Approach"),
            ModuleEntity(id = 108, subjectId = subjectAOA.id, moduleName = "Backtracking and Branch and bound"),
            ModuleEntity(id = 109, subjectId = subjectAOA.id, moduleName = "String Matching Algorithms"),
            //DBMS
            ModuleEntity(id = 110, subjectId = subjectDBMS.id, moduleName = "Introduction Database Concepts"),
            ModuleEntity(id = 111, subjectId = subjectDBMS.id, moduleName = "Entity–Relationship Data Model"),
            ModuleEntity(id = 112, subjectId = subjectDBMS.id, moduleName = "Relational Model and relational Algebra"),
            ModuleEntity(id = 113, subjectId = subjectDBMS.id, moduleName = "Structured Query Language (SQL) "),
            ModuleEntity(id = 114, subjectId = subjectDBMS.id, moduleName = "Relational-Database Design"),
            ModuleEntity(id = 115, subjectId = subjectDBMS.id, moduleName = "Transactions Management and Concurrency and Recovery "),
            //OS
            ModuleEntity(id = 116, subjectId = subjectOS.id, moduleName = "Operating system Overview"),
            ModuleEntity(id = 117, subjectId = subjectOS.id, moduleName = "Process and Process Scheduling"),
            ModuleEntity(id = 118, subjectId = subjectOS.id, moduleName = "Process Synchronization and Deadlocks"),
            ModuleEntity(id = 119, subjectId = subjectOS.id, moduleName = "Memory Management"),
            ModuleEntity(id = 120, subjectId = subjectOS.id, moduleName = "File Management"),
            ModuleEntity(id = 121, subjectId = subjectOS.id, moduleName = "I/O management"),
            //MP
            ModuleEntity(id = 122, subjectId = subjectMP.id, moduleName = "The Intel Microprocessors 8086 Architecture"),
            ModuleEntity(id = 123, subjectId = subjectMP.id, moduleName = "Instruction Set and Programming"),
            ModuleEntity(id = 124, subjectId = subjectMP.id, moduleName = "Memory and Peripherals interfacing"),
            ModuleEntity(id = 125, subjectId = subjectMP.id, moduleName = "Intel 80386DX Processor"),
            ModuleEntity(id = 126, subjectId = subjectMP.id, moduleName = "Pentium Processor"),
            ModuleEntity(id = 127, subjectId = subjectMP.id, moduleName = "Pentium 4"),

            //SEMESTER 5

            //TCS
            ModuleEntity(id = 128, subjectId = subjectTCS.id, moduleName = "Basic Concepts and Finite Automata"),
            ModuleEntity(id = 129, subjectId = subjectTCS.id, moduleName = "Regular Expressions and Languages"),
            ModuleEntity(id = 130, subjectId = subjectTCS.id, moduleName = "Grammars"),
            ModuleEntity(id = 131, subjectId = subjectTCS.id, moduleName = "Pushdown Automata(PDA)"),
            ModuleEntity(id = 132, subjectId = subjectTCS.id, moduleName = "Turing Machine (TM)"),
            ModuleEntity(id = 133, subjectId = subjectTCS.id, moduleName = "Undecidability"),
            //DWM
            ModuleEntity(id = 134, subjectId = subjectDWM.id, moduleName = "Data Warehousing Fundamentals"),
            ModuleEntity(id = 135, subjectId = subjectDWM.id, moduleName = "Introduction to Data Mining, Data Exploration and Data Pre-processing"),
            ModuleEntity(id = 136, subjectId = subjectDWM.id, moduleName = "Classification"),
            ModuleEntity(id = 137, subjectId = subjectDWM.id, moduleName = "Clustering"),
            ModuleEntity(id = 138, subjectId = subjectDWM.id, moduleName = "Mining frequent patterns and associations"),
            ModuleEntity(id = 139, subjectId = subjectDWM.id, moduleName = "Web Mining"),
            //SE
            ModuleEntity(id = 140, subjectId = subjectSE.id, moduleName = "Introduction To Software Engineering and Process Models"),
            ModuleEntity(id = 141, subjectId = subjectSE.id, moduleName = "Software Requirements Analysis and Modeling "),
            ModuleEntity(id = 142, subjectId = subjectSE.id, moduleName = "Software Estimation Metrics"),
            ModuleEntity(id = 143, subjectId = subjectSE.id, moduleName = "Software Design"),
            ModuleEntity(id = 144, subjectId = subjectSE.id, moduleName = "Software Testing"),
            ModuleEntity(id = 145, subjectId = subjectSE.id, moduleName = "Software Configuration Management"),
            //CN
            ModuleEntity(id = 146, subjectId = subjectCN.id, moduleName = "Introduction to Networking"),
            ModuleEntity(id = 147, subjectId = subjectCN.id, moduleName = "Physical Layer"),
            ModuleEntity(id = 148, subjectId = subjectCN.id, moduleName = "Data Link Layer"),
            ModuleEntity(id = 149, subjectId = subjectCN.id, moduleName = "Network layer"),
            ModuleEntity(id = 150, subjectId = subjectCN.id, moduleName = "Transport Layer"),
            ModuleEntity(id = 151, subjectId = subjectCN.id, moduleName = "Application Layer"),



        )
        modules.forEach { moduleDao.insertModule(it) }




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
            PastYearPaperEntity(id = 16, subjectId = subjectChemistry1.id, year = 2021, month = "May", pdfUrl = "Notes/Maths-1/Complex_Numbers/1727866614544.pdf")

            )

        pastYearPapers.forEach { pastYearPaperDao.insertPastYearPaper(it) }



    }
}