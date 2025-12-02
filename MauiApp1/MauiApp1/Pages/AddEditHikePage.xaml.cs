using MauiApp1.Models;
using MauiApp1.Services;

namespace MauiApp1.Pages;

public partial class AddEditHikePage : ContentPage
{
    private readonly DatabaseHelper _dbHelper;
    private Hike? _hike;
    private bool _isEditMode;

    public AddEditHikePage()
    {
        InitializeComponent();
        _dbHelper = new DatabaseHelper();
        DatePickerHike.Date = DateTime.Today;
    }

    public AddEditHikePage(long hikeId) : this()
    {
        _isEditMode = true;
        Title = "Edit Hike";
        BtnSubmit.Text = "Update Hike";
        LoadHikeData(hikeId);
    }

    private async void LoadHikeData(long hikeId)
    {
        _hike = await _dbHelper.GetHikeAsync(hikeId);
        if (_hike == null)
        {
            await DisplayAlert("Error", "Hike not found", "OK");
            await Navigation.PopAsync();
            return;
        }

        EntryName.Text = _hike.Name;
        EntryLocation.Text = _hike.Location;
        if (DateTime.TryParse(_hike.Date, out var date))
        {
            DatePickerHike.Date = date;
        }
        EntryLength.Text = _hike.Length;
        EditorDescription.Text = _hike.Description ?? string.Empty;
        EntryWeather.Text = _hike.Weather ?? string.Empty;
        EntryDuration.Text = _hike.EstimatedDuration ?? string.Empty;

        RadioYes.IsChecked = _hike.ParkingAvailable.Equals("Yes", StringComparison.OrdinalIgnoreCase);
        RadioNo.IsChecked = !RadioYes.IsChecked;

        if (!string.IsNullOrEmpty(_hike.Difficulty))
        {
            var difficultyIndex = PickerDifficulty.ItemsSource.Cast<string>()
                .ToList()
                .FindIndex(d => d.Equals(_hike.Difficulty, StringComparison.OrdinalIgnoreCase));
            if (difficultyIndex >= 0)
            {
                PickerDifficulty.SelectedIndex = difficultyIndex;
            }
        }
    }


    private async void OnSubmitClicked(object? sender, EventArgs e)
    {
        if (!ValidateInput())
        {
            return;
        }

        var name = EntryName.Text.Trim();
        var location = EntryLocation.Text.Trim();
        var date = DatePickerHike.Date.ToString("yyyy-MM-dd");
        var parking = RadioYes.IsChecked ? "Yes" : "No";
        var length = EntryLength.Text.Trim();
        var difficulty = PickerDifficulty.SelectedItem?.ToString() ?? string.Empty;
        var description = EditorDescription.Text?.Trim() ?? string.Empty;
        var weather = EntryWeather.Text?.Trim() ?? string.Empty;
        var duration = EntryDuration.Text?.Trim() ?? string.Empty;

        var details = $"Name: {name}\n" +
                     $"Location: {location}\n" +
                     $"Date: {date}\n" +
                     $"Parking: {parking}\n" +
                     $"Length: {length}\n" +
                     $"Difficulty: {difficulty}\n";

        if (!string.IsNullOrEmpty(description))
        {
            details += $"Description: {description}\n";
        }
        if (!string.IsNullOrEmpty(weather))
        {
            details += $"Weather: {weather}\n";
        }
        if (!string.IsNullOrEmpty(duration))
        {
            details += $"Estimated Duration: {duration}\n";
        }

        var confirmed = await DisplayAlert("Confirm Hike Details", details, "Confirm", "Edit");
        if (!confirmed)
        {
            return;
        }

        if (_isEditMode && _hike != null)
        {
            _hike.Name = name;
            _hike.Location = location;
            _hike.Date = date;
            _hike.ParkingAvailable = parking;
            _hike.Length = length;
            _hike.Difficulty = difficulty;
            _hike.Description = description;
            _hike.Weather = weather;
            _hike.EstimatedDuration = duration;

            var result = await _dbHelper.UpdateHikeAsync(_hike);
            if (result > 0)
            {
                await DisplayAlert("Success", "Hike updated successfully!", "OK");
                await Navigation.PopAsync();
            }
            else
            {
                await DisplayAlert("Error", "Error updating hike", "OK");
            }
        }
        else
        {
            var hike = new Hike
            {
                Name = name,
                Location = location,
                Date = date,
                ParkingAvailable = parking,
                Length = length,
                Difficulty = difficulty,
                Description = description,
                Weather = weather,
                EstimatedDuration = duration
            };

            var id = await _dbHelper.AddHikeAsync(hike);
            if (id > 0)
            {
                await DisplayAlert("Success", "Hike saved successfully!", "OK");
                await Navigation.PopAsync();
            }
            else
            {
                await DisplayAlert("Error", "Error saving hike", "OK");
            }
        }
    }

    private bool ValidateInput()
    {
        if (string.IsNullOrWhiteSpace(EntryName.Text))
        {
            DisplayAlert("Validation Error", "Name is required", "OK");
            EntryName.Focus();
            return false;
        }

        if (string.IsNullOrWhiteSpace(EntryLocation.Text))
        {
            DisplayAlert("Validation Error", "Location is required", "OK");
            EntryLocation.Focus();
            return false;
        }

        // Date is always selected from DatePicker, so no validation needed

        if (string.IsNullOrWhiteSpace(EntryLength.Text))
        {
            DisplayAlert("Validation Error", "Length is required", "OK");
            EntryLength.Focus();
            return false;
        }

        if (PickerDifficulty.SelectedItem == null)
        {
            DisplayAlert("Validation Error", "Please select difficulty level", "OK");
            return false;
        }

        return true;
    }
}

