/**
 * Import function triggers from their respective submodules:
 *
 * import {onCall} from "firebase-functions/v2/https";
 * import {onDocumentWritten} from "firebase-functions/v2/firestore";
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

import {onRequest} from "firebase-functions/v2/https";
import {onSchedule} from "firebase-functions/v2/scheduler";
import axios from "axios";
import {initializeApp} from "firebase-admin/app";
import {getFirestore, Timestamp} from "firebase-admin/firestore";

initializeApp();

// Start writing functions
// https://firebase.google.com/docs/functions/typescript

export const getTomorrowTvProgramData = onSchedule("every day 00:00",
    async () => {
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate()+1);
        const dateString = tomorrow.toISOString().split("T")[0];
        const url = `https://epg-api.video.globo.com/programmes/1337?date=${dateString}`;

        try {
            const apiResponse = await axios.get(url);

            // Extract relevant data from the response
            const entries = apiResponse.data.programme.entries;

            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            const firestoreData = entries.map((entry: any) => ({
                name: entry.program.name,
                startTime: new Timestamp(entry.start_time, 0),
            }));

            // Firestore collection reference
            const firestoreCollectionRef =
                getFirestore().collection(`guides/${tomorrow}/programs`);

            // Use batched writes for more efficient Firestore operations
            const batch = getFirestore().batch();

            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            firestoreData.forEach((data: any) => {
                const docRef = firestoreCollectionRef.doc();
                batch.set(docRef, data);
            });

            // Commit the batched writes
            await batch.commit();
        } catch (error) {
            console.error("Error retrieving TV program data:", error);
        }
    });

export const addRecentPrograms = onRequest({cors: true},
    async (request, response) => {
    try {
        // Loop over the past three days, current day, and next three days
        for (let daysOffset = -3; daysOffset <= 3; daysOffset++) {
            const currentDate = new Date();
            currentDate.setDate(currentDate.getDate() + daysOffset);

            // Format the date in "YYYY-MM-DD" format
            const formattedDate = currentDate.toISOString().split("T")[0];

            // Build the URL with the current date
            const url = `https://epg-api.video.globo.com/programmes/1337?date=${formattedDate}`;

            try {
                const apiResponse = await axios.get(url);

                // Check if the response status code is not OK
                // Skip processing for this day and move to the next iteration
                if (apiResponse.status !== 200) {
                    // eslint-disable-next-line max-len
                    console.warn(`API call for ${formattedDate} returned status code: ${apiResponse.status}. Skipping.`);
                    continue;
                }

                // Extract relevant data from the response
                const entries = apiResponse.data.programme.entries;

                // Prepare data to be stored in Firestore
                // eslint-disable-next-line @typescript-eslint/no-explicit-any
                const firestoreData = entries.map((entry: any) => ({
                    name: entry.program.name,
                    startTime: new Timestamp(entry.start_time, 0),
                }));

                // Firestore collection reference
                const firestoreCollectionRef =
                    getFirestore().
                    collection(`guides/${formattedDate}/programs`);

                // Use batched writes for more efficient Firestore operations
                const batch = getFirestore().batch();

                // eslint-disable-next-line @typescript-eslint/no-explicit-any
                firestoreData.forEach((data: any) => {
                    const docRef = firestoreCollectionRef.doc();
                    batch.set(docRef, data);
                });

                // Commit the batched writes
                await batch.commit();
            } catch (error) {
                console.error(`Error processing ${formattedDate}:`, error);

                // Skip processing for this day and move to the next iteration
                continue;
            }
        }

        response.send("Updated recent programs successfully!");
    } catch (error) {
        console.error("Error retrieving and storing recent program data:",
        error);
        response.status(500).send("Internal Server Error");
    } finally {
        response.end();
    }
});
