# Dataset Management for Germanicus

This document explains the different ways to manage the German word dataset in the Germanicus app.

## 📊 Current Implementation: CSV File

The app now uses a CSV file located at `app/src/main/assets/german_words.csv` to store word data.

### CSV Format
```csv
german,english,gender,difficulty
Haus,house,NEUTER,EASY
Auto,car,NEUTER,EASY
Buch,book,NEUTER,EASY
```

### How to Edit the Dataset

1. **Open the CSV file**: Navigate to `app/src/main/assets/german_words.csv`
2. **Add new words**: Add new rows following the format above
3. **Edit existing words**: Modify any cell in the CSV
4. **Rebuild the app**: The changes will be reflected after rebuilding

### CSV Structure
- **german**: The German word
- **english**: The English translation
- **gender**: MASCULINE, FEMININE, or NEUTER
- **difficulty**: EASY, MEDIUM, or HARD

## 🔄 Alternative Approaches

### 1. JSON File
```json
{
  "words": [
    {
      "german": "Haus",
      "english": "house",
      "gender": "NEUTER",
      "difficulty": "EASY"
    }
  ]
}
```

### 2. SQLite Database
- More complex but allows for dynamic updates
- Better for large datasets
- Requires database management code

### 3. Remote API
- Fetch words from a server
- Allows for real-time updates
- Requires internet connection

### 4. Excel/Google Sheets
- Export as CSV from spreadsheet
- Easy to manage in familiar interface
- Good for non-technical users

## 🛠️ Implementation Options

### Option A: Keep CSV (Current)
**Pros:**
- Simple to edit
- No external dependencies
- Version controlled
- Easy to backup

**Cons:**
- Requires app rebuild for changes
- Limited to static data

### Option B: JSON File
**Pros:**
- More structured than CSV
- Better for complex data
- Easy to parse

**Cons:**
- Still requires app rebuild
- More verbose than CSV

### Option C: SQLite Database
**Pros:**
- Dynamic updates possible
- Better performance for large datasets
- Complex queries possible

**Cons:**
- More complex implementation
- Requires database management

### Option D: Remote API
**Pros:**
- Real-time updates
- No app rebuilds needed
- Centralized data management

**Cons:**
- Requires internet connection
- More complex implementation
- Server maintenance needed

## 📝 Adding New Words

### Method 1: Edit CSV File
1. Open `app/src/main/assets/german_words.csv`
2. Add new row: `NeueWort,new word,MASCULINE,EASY`
3. Rebuild app

### Method 2: Use Spreadsheet
1. Open CSV in Excel/Google Sheets
2. Add new words in spreadsheet format
3. Export as CSV
4. Replace the file in assets folder
5. Rebuild app

### Method 3: Programmatic Addition
```kotlin
// In CsvWordLoader.kt
private fun getFallbackWords(): List<GermanWord> {
    return listOf(
        GermanWord("NeueWort", "new word", Gender.MASCULINE, Difficulty.EASY),
        // Add more words here
    )
}
```

## 🎯 Recommendations

### For Development/Testing
- **Use CSV**: Simple, fast, version controlled

### For Production with Small Dataset
- **Use CSV**: Easy to manage, no dependencies

### For Production with Large Dataset
- **Use SQLite**: Better performance, dynamic updates

### For Multi-user/Remote Updates
- **Use Remote API**: Centralized management, real-time updates

## 🔧 Future Enhancements

1. **Admin Panel**: Web interface to manage words
2. **User Contributions**: Allow users to suggest new words
3. **Difficulty Progression**: Track user progress and adjust difficulty
4. **Categories**: Add word categories (food, animals, etc.)
5. **Audio Files**: Include pronunciation audio files

## 📋 CSV Template

```csv
german,english,gender,difficulty
Word1,translation1,GENDER1,DIFFICULTY1
Word2,translation2,GENDER2,DIFFICULTY2
```

**Valid Values:**
- **gender**: MASCULINE, FEMININE, NEUTER
- **difficulty**: EASY, MEDIUM, HARD 