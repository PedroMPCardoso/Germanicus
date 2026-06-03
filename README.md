# Germanicus - German Learning Game

A fun and educational Android app for learning German vocabulary and grammar through interactive games.

## Features

### 🎮 Three Game Modes

1. **Gender Guessing Mode**
   - Guess the correct gender (der, die, das) of German words
   - Color-coded buttons for each gender
   - Immediate feedback with correct answers
   - Score tracking throughout the game

2. **Translation Mode**
   - Translate German words to English
   - Type your answer and submit
   - Case-insensitive answer checking
   - Real-time feedback

3. **Word Completion Mode**
   - See an English word and build the German translation
   - Select from shuffled letter tiles instead of typing with the keyboard
   - Includes extra decoy letters for challenge
   - Uses German words with 12 characters or fewer

### 🎯 Game Features

- **Modern UI Design**: Clean, card-based interface with Material Design
- **Score Tracking**: Real-time score display and final results
- **Progressive Difficulty**: Words categorized by difficulty level
- **Comprehensive Word Database**: 1,500+ German words with translations and genders
- **Responsive Design**: Works on different screen sizes

### 🎨 Visual Design

- **Color-coded Gender System**:
  - Blue for masculine (der)
  - Pink for feminine (die) 
  - Green for neuter (das)
- **Modern Card Layout**: Clean, elevated cards for better UX
- **Smooth Navigation**: Fragment-based navigation with animations

## How to Play

1. **Start the App**: Launch Germanicus from your Android device
2. **Choose Game Mode**: Select either "Gender Guessing" or "Translation"
3. **Play the Game**:
   - **Gender Guessing**: Tap the correct gender button (der/die/das)
   - **Translation**: Type the English translation and tap "Submit"
4. **Review Results**: See your score and percentage at the end
5. **Play Again**: Choose to replay or return to main menu

## Technical Details

- **Language**: Kotlin
- **Architecture**: MVVM with ViewModel and LiveData
- **Navigation**: Android Navigation Component
- **UI**: Material Design components
- **Minimum SDK**: API 24 (Android 7.0)

## Word Categories

The app includes German words across three difficulty levels:

- **Easy**: Basic nouns like Haus (house), Auto (car), Buch (book)
- **Medium**: Common words like Computer, Telefon, Zeitung
- **Hard**: Complex words like Geschichte, Wissenschaft, Entwicklung

## Future Game Ideas

- **Plural Form Guessing**: Show a singular noun and ask for the plural form.
- **Article + Noun Guessing**: Show a German noun and ask for the full form with article.
- **English to German Translation**: Show the English word and ask for the German equivalent.
- **Verb Conjugation**: Show an infinitive plus a pronoun and ask for the conjugated verb.
- **Past Participle Practice**: Show an infinitive and ask for the past participle.
- **Separable Prefix Verbs**: Identify separable verbs or complete sentences with the prefix.
- **Case Articles**: Practice nominative, accusative, dative, and genitive article forms.
- **Dative/Accusative Prepositions**: Guess which case a preposition requires.
- **Adjective Endings**: Complete adjective endings in short noun phrases.
- **Word Category Guessing**: Guess whether a word is a noun, verb, adjective, adverb, or preposition.
- **Opposites and Synonyms**: Practice related vocabulary through antonyms and synonyms.
- **Compound Noun Breakdown**: Split compound nouns into their component meanings.
- **Sentence Ordering**: Reorder scrambled words into a correct German sentence.
- **Listening and Spelling**: Hear or view pronunciation prompts and type the German spelling.
- **False Friends**: Practice words that look similar to English words but have different meanings.
- **Category Quiz**: Guess the semantic category, such as food, body, travel, or professions.

## Translation Vocabulary Ideas

- **Adjectives**: Common descriptive words like colors, sizes, feelings, temperature, speed, quality, and personality.
- **Adverbs**: Frequency, time, place, and manner words like often, already, here, outside, slowly, and together.
- **Prepositions**: Common location, direction, time, and case-based prepositions.
- **Pronouns and Determiners**: Personal pronouns, possessives, demonstratives, and question words.
- **Numbers and Quantities**: Cardinal numbers, ordinal numbers, fractions, prices, and measurement words.
- **Time and Calendar Words**: Days, months, seasons, holidays, clock time, and scheduling phrases.
- **Travel and Directions**: Airport, train, hotel, navigation, tickets, transport, and tourism vocabulary.
- **School and Work**: Classroom objects, subjects, office tools, meetings, tasks, and workplace roles.
- **Health and Emergencies**: Symptoms, body states, medicine, appointments, hospital terms, and emergency phrases.
- **Shopping and Services**: Stores, payments, sizes, returns, repairs, appointments, and customer-service words.
- **Home and Daily Routine**: Chores, appliances, rooms, hygiene, getting ready, and household objects.
- **Technology and Media**: Devices, apps, internet, email, photos, videos, and common technical actions.
- **Nature and Weather**: Landscapes, plants, animals, weather states, climate, and outdoor activities.
- **Emotions and Social Life**: Feelings, relationships, invitations, opinions, agreement, and disagreement.
- **Useful Phrases**: Short everyday chunks like "I would like", "How much is it?", and "Where is...?".

## Development

This app demonstrates modern Android development practices:
- ViewBinding for safe view access
- Navigation Component for screen management
- ViewModel for state management
- LiveData for reactive UI updates
- Material Design for consistent theming

## Background Music

Add background music files to `app/src/main/res/raw/` using lowercase underscore names, such as `main_theme.mp3` or `study_loop.mp3`. The app automatically loads every raw resource in that folder, shuffles them, and plays them as background music while the app is visible. If the folder is empty, music playback is skipped.

---

**Version**: 1.0  
**Target Audience**: German language learners of all levels 
