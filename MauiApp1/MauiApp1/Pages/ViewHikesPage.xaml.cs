using MauiApp1.Models;
using MauiApp1.Services;

namespace MauiApp1.Pages;

public partial class ViewHikesPage : ContentPage
{
    private readonly DatabaseHelper _dbHelper;

    public ViewHikesPage()
    {
        InitializeComponent();
        _dbHelper = new DatabaseHelper();
    }

    protected override async void OnAppearing()
    {
        base.OnAppearing();
        await LoadHikes();
    }

    private async Task LoadHikes()
    {
        var hikes = await _dbHelper.GetAllHikesAsync();
        HikesCollectionView.ItemsSource = hikes;

        if (hikes.Count == 0)
        {
            await DisplayAlert("Info", "No hikes found", "OK");
        }
    }

    private async void OnHikeTapped(object? sender, TappedEventArgs e)
    {
        if (sender is Frame frame && frame.BindingContext is Hike selectedHike)
        {
            var action = await DisplayActionSheet(
                $"Hike: {selectedHike.Name}",
                "Cancel",
                null,
                "View Details",
                "Edit",
                "Delete");

            switch (action)
            {
                case "View Details":
                    await ShowHikeDetails(selectedHike);
                    break;
                case "Edit":
                    await Navigation.PushAsync(new AddEditHikePage(selectedHike.Id));
                    break;
                case "Delete":
                    await DeleteHike(selectedHike);
                    break;
            }
        }
    }

    private async void OnHikeSelected(object? sender, SelectionChangedEventArgs e)
    {
        if (e.CurrentSelection.FirstOrDefault() is Hike selectedHike)
        {
            HikesCollectionView.SelectedItem = null;

            var action = await DisplayActionSheet(
                $"Hike: {selectedHike.Name}",
                "Cancel",
                null,
                "View Details",
                "Edit",
                "Delete");

            switch (action)
            {
                case "View Details":
                    await ShowHikeDetails(selectedHike);
                    break;
                case "Edit":
                    await Navigation.PushAsync(new AddEditHikePage(selectedHike.Id));
                    break;
                case "Delete":
                    await DeleteHike(selectedHike);
                    break;
            }
        }
    }

    private async Task ShowHikeDetails(Hike hike)
    {
        var details = $"Name: {hike.Name}\n" +
                     $"Location: {hike.Location}\n" +
                     $"Date: {hike.Date}\n" +
                     $"Parking: {hike.ParkingAvailable}\n" +
                     $"Length: {hike.Length}\n" +
                     $"Difficulty: {hike.Difficulty}\n";

        if (!string.IsNullOrEmpty(hike.Description))
        {
            details += $"Description: {hike.Description}\n";
        }
        if (!string.IsNullOrEmpty(hike.Weather))
        {
            details += $"Weather: {hike.Weather}\n";
        }
        if (!string.IsNullOrEmpty(hike.EstimatedDuration))
        {
            details += $"Estimated Duration: {hike.EstimatedDuration}\n";
        }

        await DisplayAlert("Hike Details", details, "OK");
    }

    private async Task DeleteHike(Hike hike)
    {
        var confirmed = await DisplayAlert(
            "Delete Hike",
            $"Are you sure you want to delete '{hike.Name}'?",
            "Yes",
            "No");

        if (confirmed)
        {
            var result = await _dbHelper.DeleteHikeAsync(hike.Id);
            if (result > 0)
            {
                await DisplayAlert("Success", "Hike deleted successfully!", "OK");
                await LoadHikes();
            }
            else
            {
                await DisplayAlert("Error", "Error deleting hike", "OK");
            }
        }
    }

    private async void OnClearAllClicked(object? sender, EventArgs e)
    {
        var confirmed = await DisplayAlert(
            "Reset Database",
            "Are you sure you want to delete all hikes? This action cannot be undone.",
            "Yes",
            "No");

        if (confirmed)
        {
            var result = await _dbHelper.DeleteAllHikesAsync();
            await DisplayAlert("Success", "All hikes deleted", "OK");
            await LoadHikes();
        }
    }
}

